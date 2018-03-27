# Aplikasi Streaming Twitter consume ke Kafka Kemudian di Save ke Hbase menggunakan apache Spark
## 1. Kebutuhan yang perlu disiapkan :
- IDE Eclipse, Intelij IDEA, etc.
- PC terInstall kafka Hadoop dan Hbase.
- Token Twitter.

## 2. Buat project maven. 
### a. edit pom.xml sbb :
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>magang</groupId>
    <artifactId>streamTwitter</artifactId>
    <version>1.0-SNAPSHOT</version>
    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
        </plugins>
    </build>

    <dependencies>
      // untuk stream tweet
        <dependency>
            <groupId>org.twitter4j</groupId>
            <artifactId>twitter4j-stream</artifactId>
            <version>4.0.6</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.25</version>
        </dependency>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-simple</artifactId>
            <version>1.7.12</version>
        </dependency>
      // library json
        <dependency>
            <groupId>org.json</groupId>
            <artifactId>json</artifactId>
            <version>20160810</version>
        </dependency>
      // untuk straming dari kafka ke spark 
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-streaming_2.11</artifactId>
            <version>2.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-core_2.11</artifactId>
            <version>2.2.0</version>
        </dependency>
        <dependency>
            <groupId>org.apache.spark</groupId>
            <artifactId>spark-streaming-kafka-0-10_2.11</artifactId>
            <version>2.2.0</version>
        </dependency>
      // untuk hbase
        <dependency>
            <groupId>org.apache.hbase</groupId>
            <artifactId>hbase-client</artifactId>
            <version>1.2.0</version>
        </dependency>

        <dependency>
            <groupId>org.apache.hbase</groupId>
            <artifactId>hbase-server</artifactId>
            <version>1.2.0</version>
        </dependency>
    </dependencies>
</project>
```
### b. buat Class untuk streaming (StreamTwitter.java)
```
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Properties;

public class StreamTwitter {
    public static void main(String[] args){
    	// key di dapatkan dari https://apps.twitter.com melalui login twitter kalian masing masing
        final String consumerKey="Oqb9bywHRcbjinLZPCDIB4lX2";
        final String consumerSecret="7fFYDQKAWGOuNI79nGrNNtI0qrRYLbyt6cEdJVlcWNWgmUUC2F";
        final String consumerToken="1297424004-4Jm7sXUJ9IYJg39O8jrtmmaERfuicyrAQXa6Kmd";
        final String consumerTokenSecret="iVfN1gnD2HgXmqajulZYXkgLBGYQ34I9TLh9AE5Z7EnIC";
	
	// keyword yang kalian gunakan untuk mencari tweet
        String[] keyWords = {"jokowi","joko widodo","presiden"};

        FilterQuery qry = new FilterQuery();
        qry.track(keyWords);

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(consumerToken).setOAuthAccessTokenSecret(consumerTokenSecret);

        TwitterStream twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance();

        StatusListener statusListener = null;
        twitterStream.addListener(new StatusListener() {
		// method yang akan di jalankan ketika mendapatkan status baru atau tweet baru
            public void onStatus(Status status) {
                JSONObject message = new JSONObject(status);  // mengubah status menjadi JSONObject
                sendKafka(""+message); // memanggil method send kafka
                System.out.println("."); // tampil titik ketika sukses
                //System.out.println(message);
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

            }

            public void onTrackLimitationNotice(int i) {

            }

            public void onScrubGeo(long l, long l1) {

            }

            public void onStallWarning(StallWarning stallWarning) {

            }

            public void onException(Exception e) {

            }
        });
        twitterStream.filter(qry);
    }
    public static void sendKafka(String pesan){ // method sendKafka dengan parameter String
        String BOOTSTRAP_SERVERS = "localhost:9092"; // di isi localhost karena menggunakan lokal kafka server
        String topic = "mytopic"; // topik pada kafka server di atas

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        Producer<String, String> producer = new KafkaProducer<String, String>(props);
        try {
            producer.send(new ProducerRecord<String, String>(topic, pesan)); // memasukkan ke kafka
            producer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```
### c. buat Class untuk consume dari kafka ke spark (ConsumeSpark.java)
```
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import scala.Tuple2;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConsumeSpark implements Serializable {
    private static SaveToHbase HS = new SaveToHbase(); // objek untuk service hbase
    public static void main(String[] args) throws InterruptedException {
//		SparkConf conf = new SparkConf();
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("SteramKafka"); // stream pada local 2
        JavaStreamingContext streamingContext = new JavaStreamingContext(conf, new Duration(1000));

        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.OFF);

        Map<String, Object> kafkaParams = new HashMap<>();

        kafkaParams.put("bootstrap.servers", "l=localhost:9092"); // kafka server
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "tester");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);

        Collection<String> topics = Arrays.asList("mytopic"); // topik yang digunakan

        JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(streamingContext,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams));

        stream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));
	
	//method call untuk membuat method  mengembalikan nilai json
        JavaDStream<String> lines = stream.map(new Function<ConsumerRecord<String, String>, String>() {

            @Override
            public String call(ConsumerRecord<String, String> v1) throws Exception {
                return v1.value();
            }
        });

		// perulangan untuk java rdd
		lines.foreachRDD(new VoidFunction<JavaRDD<String>>() {
			@Override
			public void call(JavaRDD<String> arg0) throws Exception {

				try {
				//memanggil method setInit dari objek HS
				    HS.setInit();
				    	//memanggil method insertHbase dari objek HS dengan parameter javardd
					HS.insertHbase(arg0);

				} catch (Exception e) {
				}
			}
		});

	// start
        lines.print(1);
        streamingContext.start();
        streamingContext.awaitTermination();
    }
}

```
### d. buat Class untuk Servicehbase (SaveToHbase.java)
```
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.io.ImmutableBytesWritable;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.mapreduce.Job;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.function.PairFunction;
import scala.Tuple2;
import twitter4j.JSONObject;

import java.io.IOException;
import java.io.Serializable;

public class SaveToHbase implements Serializable {
    private transient Job newAPIJobConfiguration;
    final static String HMASTER = "localhost:16000"; // sesuaikan dengan HMASTER server dan port kalian
    final static String ZOOKEEPER = "127.0.0.1";  // ip zookeeper

	// method untuk konfigurasi
    public SaveToHbase setInit() throws IOException {
        Configuration config =  HBaseConfiguration.create();
        config.set("hbase.master", HMASTER);
//		config.set("zookeeper.znode.parent", "/hbase-unsecure");
        config.setInt("timeout", 5000);
        config.set("hbase.zookeeper.quorum", ZOOKEEPER);
        config.set(TableOutputFormat.OUTPUT_TABLE, "twitter"); // disini menggunakan tabel hbase dengan nama "twitter"

        Job newAPIJobConfiguration1 = Job.getInstance(config);
        newAPIJobConfiguration1.getConfiguration().set(TableOutputFormat.OUTPUT_TABLE, "twitter"); // tabel "twitter"
        newAPIJobConfiguration1.setOutputFormatClass(TableOutputFormat.class);
        this.newAPIJobConfiguration = newAPIJobConfiguration1;
        return this;
    }

    public void insertHbase(JavaRDD<String> saveHb) throws IOException {
        System.out.println("ini data ==> "+saveHb.collect()); // menggunakan collect untuk menampilkan nilai parameternya "JSON"
	// mapping untuk inputan ke database
        JavaPairRDD<ImmutableBytesWritable, Put> putData = saveHb
                .mapToPair(new PairFunction<String, ImmutableBytesWritable, Put>() {
                    public Tuple2<ImmutableBytesWritable, Put> call(String value) throws Exception {

                        //jadiin object
                        JSONObject val = new JSONObject(value);
                        System.out.println("isi data nya ==> "+val.getString("id")+"text =>"+val.getString("text"));
                        // Deklarasi Rowkey -->
                        Put p = new Put(Bytes.toBytes(val.getString("id")));


                        // Insert hbase (family, qualifier, value)
                        p.addColumn(Bytes.toBytes("idTweet"), Bytes.toBytes("data"), Bytes.toBytes(val.getString("text")));
                        System.out.println("data p => "+p);
                        return new Tuple2<ImmutableBytesWritable, Put>(new ImmutableBytesWritable(), p);
                    }
                });
        try {
            putData.saveAsNewAPIHadoopDataset(newAPIJobConfiguration.getConfiguration());
            System.out.println("input 1 row ");
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("error pas nyimpan!!");
        }
    }
}
```
# NB
#### Pada saat streaming kafka harus sudah diaktifkan, pada saat hbase di start hadoop kafka zookeeper harus sudah diaktifkan, tabel configurasi hbase service disesuikan dengan tabel kalian. 

