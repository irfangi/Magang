package magang.projectAkhir;

import java.util.Properties;

import org.apache.jasper.tagplugins.jstl.core.Out;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import manager.KriteriaTwitter;
import manager.TweetManager;
import model.Tweet;
import twitter4j.JSONException;
import twitter4j.JSONObject;

/**
 * Hello world!
 *
 */
public class App 
{
	private static final String USERNAME = "Username: ";
	private static final String RETWEETS = "Retweets: ";
	private static final String MENTIONS = "Mentions: ";
	private static final String HASHTAGS = "Hashtags: ";
	private static final String TEXT = "Text: ";
	public static void main(String[] args) {
		KriteriaTwitter kriteriaTwitter = null;
		Tweet t = null;
		
		kriteriaTwitter = KriteriaTwitter.create()
//				.setUsername("barackobama")
				.setQuerySearch( "ojol gojek")//"jokowi perekonomian politik")
				.setSince("2018-01-01")
				.setUntil("2018-02-01")
				.setMaxTweets(100);
		System.out.println(TweetManager.getTweet(kriteriaTwitter).size());
		for (int i=0;i<TweetManager.getTweet(kriteriaTwitter).size();i++) {
			t = TweetManager.getTweet(kriteriaTwitter).get(i);
			JSONObject cek = new JSONObject();
			try {
				cek.put("id", t.getId());
				cek.put("user", t.getUsername());
				cek.put("waktu", t.getDate());
				cek.put("hastag", t.getHashtags());
				cek.put("text", t.getText());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(cek);
			sendKafka(cek.toString());
		}
	}
	public static void sendKafka(String pesan){
        String BOOTSTRAP_SERVERS = "localhost:9092";
        String topic = "mytopic1";

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
