package magang.projectAkhir;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.VoidFunction;
import org.apache.spark.streaming.Duration;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import org.apache.spark.api.java.JavaRDD;

import scala.Tuple2;

import org.apache.jasper.tagplugins.jstl.core.Out;
import org.apache.kafka.clients.consumer.ConsumerRecord;

public class ConsumeSparkKafka {
	static SaveHbase sh = new SaveHbase();
	public static void main(String[] args) throws InterruptedException {
//		SparkConf conf = new SparkConf();
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("SteramKafka");
        JavaStreamingContext streamingContext = new JavaStreamingContext(conf, new Duration(10000));

        Logger rootLogger = Logger.getRootLogger();
        rootLogger.setLevel(Level.OFF);

        Map<String, Object> kafkaParams = new HashMap<String, Object>();

        kafkaParams.put("bootstrap.servers", "l=localhost:9092");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "tester");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);

        Collection<String> topics = Arrays.asList("mytopic1");

        JavaInputDStream<ConsumerRecord<String, String>> stream = KafkaUtils.createDirectStream(streamingContext,
                LocationStrategies.PreferConsistent(),
                ConsumerStrategies.<String, String>Subscribe(topics, kafkaParams));

        stream.mapToPair(record -> new Tuple2<>(record.key(), record.value()));

        JavaDStream<String> lines = stream.map(new Function<ConsumerRecord<String, String>, String>() {

            public String call(ConsumerRecord<String, String> v1) throws Exception {
            	System.out.println(v1.value());
            	return v1.value();
            }
        });

		lines.foreachRDD(new VoidFunction<JavaRDD<String>>() {
			@Override
			public void call(JavaRDD<String> arg0) throws Exception {

				try {
				    sh.setInit();
					sh.insertHbase(arg0);

				} catch (Exception e) {
				}
			}
		});

        lines.print(1);
        streamingContext.start();
        streamingContext.awaitTermination();
    }
}
