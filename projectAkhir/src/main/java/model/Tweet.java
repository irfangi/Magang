package model;

import java.util.Date;

public class Tweet {
	private String id;
	private String username;
	private String text;
	private String date;
	private int retweets;
	private String mentions;
	private String hashtags;
	private String geo;
	
	
	
	public Tweet() {
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String time) {
		this.date = time;
	}
	public int getRetweets() {
		return retweets;
	}
	public void setRetweets(int retweets) {
		this.retweets = retweets;
	}
	public String getMentions() {
		return mentions;
	}
	public void setMentions(String mentions) {
		this.mentions = mentions;
	}
	public String getHashtags() {
		return hashtags;
	}
	public void setHashtags(String hashtags) {
		this.hashtags = hashtags;
	}
	public String getGeo() {
		return geo;
	}
	public void setGeo(String geo) {
		this.geo = geo;
	}
}
