package manager;

public class KriteriaTwitter {
	private String username;
	private String since;
	private String until;
	private String querySearch;
	private int maxTweets;
	
	public KriteriaTwitter() {
	
	}
	public static KriteriaTwitter create() {
		return new KriteriaTwitter();
	}
	
	public String getUsername() {
		return username;
	}
	public KriteriaTwitter setUsername(String username) {
		this.username = username;
		return this;
	}
	public String getSince() {
		return since;
	}
	public KriteriaTwitter setSince(String since) {
		this.since = since;
		return this;
	}
	public String getUntil() {
		return until;
	}
	public KriteriaTwitter setUntil(String until) {
		this.until = until;
		return this;
	}
	public String getQuerySearch() {
		return querySearch;
	}
	public KriteriaTwitter setQuerySearch(String querySearch) {
		this.querySearch = querySearch;
		return this;
	}
	public int getMaxTweets() {
		return maxTweets;
	}
	public KriteriaTwitter setMaxTweets(int maxTweets) {
		this.maxTweets = maxTweets;
		return this;
	}
}
