package lv.valts.redditstats.statistics;

import static lv.valts.redditstats.redditevent.RedditEventType.COMMENT;
import static lv.valts.redditstats.redditevent.RedditEventType.SUBMISSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lv.valts.redditstats.redditevent.RedditEventAggregations;
import lv.valts.redditstats.redditevent.RedditEventRepository;
import lv.valts.redditstats.redditevent.SubredditActivity;

@ExtendWith(MockitoExtension.class)
class LiveStatisticsTest {
	private static final TimeRange TIME_RANGE = TimeRange.FIVE_MINUTES;
	private static final long COMMENT_COUNT = 245L;
	private static final long SUBMISSION_COUNT = 45L;
	private static final long EVENT_COUNT = 14L;
	private static final String SUBREDDIT_NAME = "some name";
	private static final SubredditActivity SUBREDDIT_ACTIVITY = createSubredditActivity();

	@Mock
	RedditEventRepository redditEventRepository;
	@Mock
	RedditEventAggregations redditEventAggregations;
	@InjectMocks
	private LiveStatistics liveStatistics;

	@Test
	void getActivity() {
		given(redditEventRepository.totalActivity(any(), eq(SUBMISSION))).willReturn(SUBMISSION_COUNT);
		given(redditEventRepository.totalActivity(any(), eq(COMMENT))).willReturn(COMMENT_COUNT);

		var activity = liveStatistics.getActivity(TIME_RANGE);

		assertThat(activity.getNumberOfSubmissions()).isEqualTo(SUBMISSION_COUNT);
		assertThat(activity.getNumberOfComments()).isEqualTo(COMMENT_COUNT);
	}

	@Test
	void getTop100Active() {
		given(redditEventAggregations.getTop100ByTotalActivity(any())).willReturn(List.of(SUBREDDIT_ACTIVITY));

		var top100 = liveStatistics.getTop100Active(TIME_RANGE);

		assertThat(top100).hasSize(1);
		var subredditTotalActivity = top100.get(0);
		assertThat(subredditTotalActivity.getSubredditName()).isEqualTo(SUBREDDIT_NAME);
		assertThat(subredditTotalActivity.getNumberOfCommentsAndSubmissions()).isEqualTo(EVENT_COUNT);
	}

	@Test
	void getMostActiveBySubmissionCount() {
		given(redditEventAggregations.getMostActive(any(), any())).willReturn(Optional.of(SUBREDDIT_ACTIVITY));

		var mostActive = liveStatistics.getMostActiveBySubmissionCount(TIME_RANGE);

		then(redditEventAggregations).should().getMostActive(any(), eq(SUBMISSION));
		assertThat(mostActive).isNotEmpty();
		assertThat(mostActive.get().getSubredditName()).isEqualTo(SUBREDDIT_NAME);
		assertThat(mostActive.get().getNumberOfSubmissions()).isEqualTo(EVENT_COUNT);
	}

	@Test
	void getMostActiveByCommentCount() {
		given(redditEventAggregations.getMostActive(any(), any())).willReturn(Optional.of(SUBREDDIT_ACTIVITY));

		var mostActive = liveStatistics.getMostActiveByCommentCount(TIME_RANGE);

		then(redditEventAggregations).should().getMostActive(any(), eq(COMMENT));
		assertThat(mostActive).isNotEmpty();
		assertThat(mostActive.get().getSubredditName()).isEqualTo(SUBREDDIT_NAME);
		assertThat(mostActive.get().getNumberOfComments()).isEqualTo(EVENT_COUNT);
	}

	private static SubredditActivity createSubredditActivity() {
		var subredditActivity = new SubredditActivity();
		subredditActivity.setSubredditName(SUBREDDIT_NAME);
		subredditActivity.setNumberOfEvents(EVENT_COUNT);
		return subredditActivity;
	}
}