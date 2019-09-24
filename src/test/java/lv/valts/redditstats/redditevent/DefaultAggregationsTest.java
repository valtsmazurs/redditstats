package lv.valts.redditstats.redditevent;

import static lv.valts.redditstats.redditevent.RedditEventType.SUBMISSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;

@ExtendWith(MockitoExtension.class)
class DefaultAggregationsTest {
	private static final long FROM_UTC_TIMESTAMP = 12L;
	private static final long EVENT_COUNT = 14L;
	private static final String FIRST_SUBREDDIT_NAME = "first";
	private static final String SECOND_SUBREDDIT_NAME = "second";
	private static final SubredditActivity FIRST_ACTIVITY = createSubredditActivity(FIRST_SUBREDDIT_NAME);
	private static final SubredditActivity SECOND_ACTIVITY = createSubredditActivity(SECOND_SUBREDDIT_NAME);

	@Mock
	private MongoTemplate mongoTemplate;
	@Mock
	private AggregationResults aggregationResults;
	@InjectMocks
	private DefaultAggregations defaultAggregations;

	@BeforeEach
	void setUp() {
		given(mongoTemplate.aggregate(any(), eq(RedditEvent.class), eq(SubredditActivity.class)))
				.willReturn(aggregationResults);
	}

	@Test
	void getsTop100ByTotalActivity() {
		given(aggregationResults.getMappedResults()).willReturn(List.of(FIRST_ACTIVITY, SECOND_ACTIVITY));

		var activities = defaultAggregations.getTop100ByTotalActivity(FROM_UTC_TIMESTAMP);

		assertThat(activities).containsExactly(FIRST_ACTIVITY, SECOND_ACTIVITY);
	}

	@Test
	void getsMostActive() {
		given(aggregationResults.getMappedResults()).willReturn(List.of(FIRST_ACTIVITY));

		var activity = defaultAggregations.getMostActive(FROM_UTC_TIMESTAMP, SUBMISSION);

		assertThat(activity).containsSame(FIRST_ACTIVITY);
	}

	@Test
	void returnsEmptyMostActiveWhenNoData() {
		given(aggregationResults.getMappedResults()).willReturn(Collections.emptyList());

		var activity = defaultAggregations.getMostActive(FROM_UTC_TIMESTAMP, SUBMISSION);

		assertThat(activity).isEmpty();
	}

	private static SubredditActivity createSubredditActivity(String subredditName) {
		var subredditActivity = new SubredditActivity();
		subredditActivity.setSubredditName(subredditName);
		subredditActivity.setNumberOfEvents(EVENT_COUNT);
		return subredditActivity;
	}
}