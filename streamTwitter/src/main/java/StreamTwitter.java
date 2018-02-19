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
        final String consumerKey="Oqb9bywHRcbjinLZPCDIB4lX2";
        final String consumerSecret="7fFYDQKAWGOuNI79nGrNNtI0qrRYLbyt6cEdJVlcWNWgmUUC2F";
        final String consumerToken="1297424004-4Jm7sXUJ9IYJg39O8jrtmmaERfuicyrAQXa6Kmd";
        final String consumerTokenSecret="iVfN1gnD2HgXmqajulZYXkgLBGYQ34I9TLh9AE5Z7EnIC";

        String[] keyWords = {"jokowi","joko widodo","presiden"};

        FilterQuery qry = new FilterQuery();
        qry.track(keyWords);

        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret)
                .setOAuthAccessToken(consumerToken).setOAuthAccessTokenSecret(consumerTokenSecret);

        TwitterStream twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance();

        StatusListener statusListener = null;
        twitterStream.addListener(new StatusListener() {
            public void onStatus(Status status) {
                JSONObject message = new JSONObject(status);
                sendKafka(""+message);
                System.out.println(".");
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
    public static void sendKafka(String pesan){
        String BOOTSTRAP_SERVERS = "localhost:9092";
        String topic = "mytopic";

        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "KafkaExampleProducer");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        Producer<String, String> producer = new KafkaProducer<String, String>(props);
        try {
            producer.send(new ProducerRecord<String, String>(topic, pesan));
            producer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
