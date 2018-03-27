package manager;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.lang.model.element.Element;
import javax.swing.text.Document;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import model.Tweet;
import twitter4j.JSONObject;
import twitter4j.JSONTokener;

public class TweetManager {
	private static final HttpClient defaultHttpClient = HttpClients.createDefault();
	static {
		Logger.getLogger("org.apache.http").setLevel(Level.OFF);
	}
	
	private static String getURLRespon(String username,String since, String until,String querySearch, String scrollCursor) throws Exception {
		String appendQuery="";
		if (username !=null) {
			appendQuery += "from:"+username;
		}
		if (since !=null) {
			appendQuery += " since:"+since;
		}
		if (until !=null) {
			appendQuery += " until:"+until;
		}
		if (querySearch !=null) {
			appendQuery += " "+querySearch;
		}
		
		String url = String.format("https://twitter.com/i/search/timeline?f=realtime&q=%s&src=typd&max_position=%s", URLEncoder.encode(appendQuery, "UTF-8"), scrollCursor);
		
		HttpGet httpGet = new HttpGet(url);
		HttpEntity httpEntity = defaultHttpClient.execute(httpGet).getEntity();
		
		return EntityUtils.toString(httpEntity);
	}
	public static List<Tweet> getTweet(KriteriaTwitter kriteriaTwitter){
		List<Tweet> results = new ArrayList<Tweet>();
		try {
			String refreshCursor= null;
			outerLace: while (true) {
				JSONObject json = new JSONObject(getURLRespon(kriteriaTwitter.getUsername(), kriteriaTwitter.getSince(), kriteriaTwitter.getUntil(), kriteriaTwitter.getQuerySearch(), refreshCursor));
				refreshCursor = json.getString("min_position");
				org.jsoup.nodes.Document doc =  Jsoup.parse((String) json.getString("items_html"));
				Elements tweets = doc.select("div.js-stream-tweet");
				
				if (tweets.size()==0) {
					break;
				}
				SimpleDateFormat date_fmt = new SimpleDateFormat("EEE MMM d kk:mm:ss Z yyyy");

				for(org.jsoup.nodes.Element tweet : tweets) {
					String usernameTweet = tweet.select("div.tweet").attr("data-screen-name");
					String txt = tweet.select("p.js-tweet-text").text().replaceAll("[^\\u0000-\\uFFFF]", "");
					int retweets = Integer.valueOf(tweet.select("span.ProfileTweet-action--retweet span.ProfileTweet-actionCount").attr("data-tweet-stat-count").replaceAll(",", ""));
					long dateMs = Long.valueOf(tweet.select("small.time span.js-short-timestamp").attr("data-time-ms"));
					String id = tweet.attr("data-tweet-id");
					String geo = "";
					
					Elements geoElement = tweet.select("span.Tweet-geo");
					if (geoElement.size() > 0) {
						geo = geoElement.attr("title");
					}

					Date date = new Date(dateMs);
					SimpleDateFormat localDateFormat = new SimpleDateFormat("HH:mm:ss");
			        String time = localDateFormat.format(date);
					
					Tweet t = new Tweet();
					
					t.setId(id);
					t.setUsername(usernameTweet);
					t.setText(txt);
					t.setDate(time);
					t.setRetweets(retweets);
					t.setGeo(geo);
					t.setMentions(processTerms("(@\\w*)", txt));
					t.setHashtags(processTerms("(#\\w*)", txt));
					
					results.add(t);
					
					if (kriteriaTwitter.getMaxTweets() > 0 && results.size() >= kriteriaTwitter.getMaxTweets()) {
						break outerLace;
					}
				}
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return results;
	}
	private static String processTerms(String pattern, String tweetText) {
		StringBuilder sb = new StringBuilder();
		Matcher matcher = Pattern.compile(pattern).matcher(tweetText);
		while (matcher.find()) {
			sb.append(matcher.group());
			sb.append(" ");
		}
		return sb.toString().trim();
	}
}
