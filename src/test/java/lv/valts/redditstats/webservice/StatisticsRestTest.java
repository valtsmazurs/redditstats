package lv.valts.redditstats.webservice;

import static lv.valts.redditstats.statistics.TimeRange.FIVE_MINUTES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lv.valts.redditstats.statistics.ImmutableRedditActivity;
import lv.valts.redditstats.statistics.RedditActivity;
import lv.valts.redditstats.statistics.ImmutableSubredditTotalActivity;
import lv.valts.redditstats.statistics.ImmutableSubredditWithCommentCount;
import lv.valts.redditstats.statistics.ImmutableSubredditWithSubmissionCount;
import lv.valts.redditstats.statistics.RedditStatistics;
import lv.valts.redditstats.statistics.SubredditTotalActivity;
import lv.valts.redditstats.statistics.SubredditWithCommentCount;
import lv.valts.redditstats.statistics.SubredditWithSubmissionCount;

@ExtendWith(MockitoExtension.class)
class StatisticsRestTest {
	private static final RedditActivity REDDIT_ACTIVITY = ImmutableRedditActivity.builder()
			.numberOfComments(1)
			.numberOfSubmissions(2)
			.build();

	private static final SubredditWithSubmissionCount SUBREDDIT_WITH_SUBMISSION_COUNT = ImmutableSubredditWithSubmissionCount
			.builder()
			.subredditName("name")
			.numberOfSubmissions(5L)
			.build();

	private static final SubredditWithCommentCount SUBREDDIT_WITH_COMMENT_COUNT = ImmutableSubredditWithCommentCount
			.builder()
			.subredditName("name")
			.numberOfComments(6L)
			.build();

	private static final SubredditTotalActivity SUBREDDIT_TOTAL_ACTIVITY = ImmutableSubredditTotalActivity.builder()
			.subredditName("some_name")
			.numberOfCommentsAndSubmissions(7L)
			.build();
	private static final List<SubredditTotalActivity> TOP_100_SUBREDDITS = List
			.of(SUBREDDIT_TOTAL_ACTIVITY, SUBREDDIT_TOTAL_ACTIVITY);

	@Mock
	private RedditStatistics redditStatistics;
	@InjectMocks
	private StatisticsRest service;

	@Test
	void proxiesActivity() {
		given(redditStatistics.getActivity(any())).willReturn(REDDIT_ACTIVITY);

		var activity = service.activity(FIVE_MINUTES);

		then(redditStatistics).should().getActivity(FIVE_MINUTES);
		assertThat(activity).isSameAs(REDDIT_ACTIVITY);
	}

	@Test
	void proxiesTop100Active() {
		given(redditStatistics.getTop100Active(any())).willReturn(TOP_100_SUBREDDITS);

		var subreddits = service.top100Active(FIVE_MINUTES);

		then(redditStatistics).should().getTop100Active(FIVE_MINUTES);
		assertThat(subreddits).isSameAs(TOP_100_SUBREDDITS);
	}

	@Test
	void proxiesMostActiveBySubmissions() {
		given(redditStatistics.getMostActiveBySubmissionCount(any())).willReturn(Optional.of(SUBREDDIT_WITH_SUBMISSION_COUNT));

		var subredditStatistics = service.mostActiveBySubmissions(FIVE_MINUTES);

		then(redditStatistics).should().getMostActiveBySubmissionCount(FIVE_MINUTES);
		assertThat(subredditStatistics).containsSame(SUBREDDIT_WITH_SUBMISSION_COUNT);
	}

	@Test
	void proxiesMostActiveByComments() {
		given(redditStatistics.getMostActiveByCommentCount(any())).willReturn(Optional.of(SUBREDDIT_WITH_COMMENT_COUNT));

		var subredditStatistics = service.mostActiveByComments(FIVE_MINUTES);

		then(redditStatistics).should().getMostActiveByCommentCount(FIVE_MINUTES);
		assertThat(subredditStatistics).containsSame(SUBREDDIT_WITH_COMMENT_COUNT);
	}
}