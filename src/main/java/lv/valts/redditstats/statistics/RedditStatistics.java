package lv.valts.redditstats.statistics;

import java.util.List;
import java.util.Optional;

public interface RedditStatistics {
	RedditActivity getActivity(TimeRange range);

	List<SubredditTotalActivity> getTop100Active(TimeRange range);

	Optional<SubredditWithSubmissionCount> getMostActiveBySubmissionCount(TimeRange range);

	Optional<SubredditWithCommentCount> getMostActiveByCommentCount(TimeRange range);
}
