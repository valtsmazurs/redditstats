package lv.valts.redditstats.statistics;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lv.valts.redditstats.redditevent.RedditEventAggregations;
import lv.valts.redditstats.redditevent.RedditEventRepository;
import lv.valts.redditstats.redditevent.RedditEventType;
import lv.valts.redditstats.redditevent.SubredditActivity;

@Service
class LiveStatistics implements RedditStatistics {
	private final RedditEventRepository redditEventRepository;
	private final RedditEventAggregations redditEventAggregations;

	@Autowired
	LiveStatistics(RedditEventRepository redditEventRepository,
			RedditEventAggregations redditEventAggregations) {
		this.redditEventRepository = redditEventRepository;
		this.redditEventAggregations = redditEventAggregations;
	}

	@Override
	public RedditActivity getActivity(TimeRange range) {
		var fromUtcTimestamp = getFromUtcTimestamp(range);
		var submissionCount = getEventCount(fromUtcTimestamp, RedditEventType.SUBMISSION);
		var commentCount = getEventCount(fromUtcTimestamp, RedditEventType.COMMENT);
		return ImmutableRedditActivity.builder()
				.numberOfSubmissions(submissionCount.intValue())
				.numberOfComments(commentCount.intValue())
				.build();
	}

	@Override
	public List<SubredditTotalActivity> getTop100Active(TimeRange range) {
		var fromUtcTimestamp = getFromUtcTimestamp(range);
		return redditEventAggregations.getTop100ByTotalActivity(fromUtcTimestamp)
				.stream()
				.map(this::subredditTotalActivityOf)
				.collect(Collectors.toList());
	}

	@Override
	public Optional<SubredditWithSubmissionCount> getMostActiveBySubmissionCount(TimeRange range) {
		var fromUtcTimestamp = getFromUtcTimestamp(range);
		return redditEventAggregations.getMostActive(fromUtcTimestamp, RedditEventType.SUBMISSION)
				.map(this::subredditWithSubmissionCountOf);
	}

	@Override
	public Optional<SubredditWithCommentCount> getMostActiveByCommentCount(TimeRange range) {
		var fromUtcTimestamp = getFromUtcTimestamp(range);
		return redditEventAggregations.getMostActive(fromUtcTimestamp, RedditEventType.COMMENT)
				.map(this::subredditWithCommentCountOf);
	}

	private Long getFromUtcTimestamp(TimeRange range) {
		var nowInUtc = ZonedDateTime.now(ZoneOffset.UTC);
		var reduceBy = RangeToTemporalAmount.of(range).transform();
		return nowInUtc.minus(reduceBy).toEpochSecond();
	}

	private Long getEventCount(Long fromUtcTimestamp, RedditEventType eventType) {
		return redditEventRepository.totalActivity(fromUtcTimestamp, eventType);
	}

	private ImmutableSubredditTotalActivity subredditTotalActivityOf(SubredditActivity subredditActivity) {
		return ImmutableSubredditTotalActivity.builder()
				.subredditName(subredditActivity.getSubredditName())
				.numberOfCommentsAndSubmissions(subredditActivity.getNumberOfEvents())
				.build();
	}

	private ImmutableSubredditWithSubmissionCount subredditWithSubmissionCountOf(SubredditActivity subredditActivity) {
		return ImmutableSubredditWithSubmissionCount.builder()
				.subredditName(subredditActivity.getSubredditName())
				.numberOfSubmissions(subredditActivity.getNumberOfEvents())
				.build();
	}

	private ImmutableSubredditWithCommentCount subredditWithCommentCountOf(SubredditActivity subredditActivity) {
		return ImmutableSubredditWithCommentCount.builder()
				.subredditName(subredditActivity.getSubredditName())
				.numberOfComments(subredditActivity.getNumberOfEvents())
				.build();
	}
}
