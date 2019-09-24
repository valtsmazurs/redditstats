package lv.valts.redditstats.redditevent;

public class SubredditActivity {
	private String subredditName;

	private Long numberOfEvents;

	public String getSubredditName() {
		return subredditName;
	}

	public void setSubredditName(String subredditName) {
		this.subredditName = subredditName;
	}

	public Long getNumberOfEvents() {
		return numberOfEvents;
	}

	public void setNumberOfEvents(Long numberOfEvents) {
		this.numberOfEvents = numberOfEvents;
	}
}
