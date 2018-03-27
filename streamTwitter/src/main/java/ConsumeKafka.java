import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import twitter4j.JSONException;

import java.util.Arrays;
import java.util.Properties;
import java.util.UUID;

public class ConsumeKafka {
    public static void main(String[] args) throws JSONException{
        Properties properties = new Properties();
        properties.put("bootstrap.servers", "localhost:9092");

        properties.put("group.id", "group." + UUID.randomUUID().toString());
        properties.put("enable.auto.commit", "true");
        properties.put("auto.commit.interval.ms", "1000");
        properties.put("auto.offset.reset", "latest");
        properties.put("session.timeout.ms", "30000");
        properties.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        properties.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");

        KafkaConsumer<String,String> consumer = new KafkaConsumer<String, String>(properties);

        consumer.subscribe(Arrays.asList("mytopic"));
        while (true){
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> record : records) {
                 System.out.println(record.value());
            }
        }
    }
}
