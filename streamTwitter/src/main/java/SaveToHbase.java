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
    final static String HMASTER = "localhost:16000";
    final static String ZOOKEEPER = "127.0.0.1";

    public SaveToHbase setInit() throws IOException {
        Configuration config =  HBaseConfiguration.create();
        config.set("hbase.master", HMASTER);
//		config.set("zookeeper.znode.parent", "/hbase-unsecure");
        config.setInt("timeout", 5000);
        config.set("hbase.zookeeper.quorum", ZOOKEEPER);
        config.set(TableOutputFormat.OUTPUT_TABLE, "twitter");

        Job newAPIJobConfiguration1 = Job.getInstance(config);
        newAPIJobConfiguration1.getConfiguration().set(TableOutputFormat.OUTPUT_TABLE, "twitter");
        newAPIJobConfiguration1.setOutputFormatClass(TableOutputFormat.class);
        this.newAPIJobConfiguration = newAPIJobConfiguration1;
        return this;
    }

    public void insertHbase(JavaRDD<String> saveHb) throws IOException {
        System.out.println("ini data ==> "+saveHb.collect());
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
