package com.oni;

import java.io.IOException;
import java.util.Properties;


import twitter4j.FilterQuery;
import twitter4j.JSONArray;
import twitter4j.JSONObject;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;


public class Streaming {
	
	public Streaming() {
			String[] keywords = {"jokowi","joko widodo","Jokowi","Joko Widodo","JOKOWI"};
			FilterQuery filterQuery = new FilterQuery();
			filterQuery.track(keywords);
			
			ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
			String oAuthConsumerKey = "jvgw26j6i0r3920BGhXcxZJYD";
			configurationBuilder.setOAuthConsumerKey(oAuthConsumerKey);
			String oAuthConsumerSecret = "JuLAgnhsZxqilLJ1YibfI8qrMZvuQ0oH4QdaoeI8B1htzOYiii";
			configurationBuilder.setOAuthConsumerSecret(oAuthConsumerSecret);
			String oAuthAccessToken = "934631846263926784-dvamNL08pcfyJqMWQAa4yQMXmFNyJ4g";
			configurationBuilder.setOAuthAccessToken(oAuthAccessToken);
			String oAuthAccessTokenSecret = "3ktzepDOsiHacAmzxtAevB7EH1UU6JIYq9wr7qyv7Jq8A";
			configurationBuilder.setOAuthAccessTokenSecret(oAuthAccessTokenSecret);
			
			TwitterStream twitterStream = new TwitterStreamFactory(configurationBuilder.build()).getInstance();
			StatusListener listener2 = null;
			twitterStream.addListener(listener2);
			twitterStream.addListener(new StatusListener() {

				public void onException(Exception arg0) {
					// TODO Auto-generated method stub
					
				}

				public void onDeletionNotice(StatusDeletionNotice arg0) {
					// TODO Auto-generated method stub
					
				}

				public void onScrubGeo(long arg0, long arg1) {
					// TODO Auto-generated method stub
					
				}

				public void onStallWarning(StallWarning arg0) {
					// TODO Auto-generated method stub
					
				}

				public void onStatus(Status arg0) {
					// TODO Auto-generated method stub
					
					Indexer indexer = new Indexer();
					indexer.indexing(arg0);
					JSONObject jsonObject = new JSONObject(arg0);
					System.out.println("Stream.....");
//					System.out.println("from -> "+arg0.getUser().getName());
//					System.out.println("value -> "+arg0.getText());
				}

				public void onTrackLimitationNotice(int arg0) {
					// TODO Auto-generated method stub
					
				}

			});
			twitterStream.filter(filterQuery);
	}	
}