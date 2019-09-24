package lv.valts.redditstats.redditevent;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
public interface RedditEvent {
	String getId();

	RedditEventType getEventType();

	String getSubredditName();

	Long getCreateTimestampUtc();
}
