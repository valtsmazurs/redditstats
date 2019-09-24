package lv.valts.redditstats.redditevent;

import java.util.List;
import java.util.Optional;

public interface RedditEventAggregations {
	List<SubredditActivity> getTop100ByTotalActivity(Long fromUtcTimestamp);

	Optional<SubredditActivity> getMostActive(Long fromUtcTimestamp, RedditEventType eventType);
}
