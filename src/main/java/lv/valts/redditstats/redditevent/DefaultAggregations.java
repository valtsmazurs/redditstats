package lv.valts.redditstats.redditevent;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
class DefaultAggregations implements RedditEventAggregations {
	private static final String CREATE_TIMESTAMP_UTC = "createTimestampUtc";
	private static final String SUBREDDIT_NAME = "subredditName";
	private static final String NUMBER_OF_EVENTS = "numberOfEvents";
	private static final String EVENT_TYPE = "eventType";
	private static final int TOP_ACTIVE_LIMIT = 100;
	private static final int SINGLE_DOCUMENT_LIMIT = 1;

	private final MongoTemplate mongoTemplate;

	@Autowired
	DefaultAggregations(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public List<SubredditActivity> getTop100ByTotalActivity(Long fromUtcTimestamp) {
		var criteria = searchByTimestamp(fromUtcTimestamp);
		return getAggregates(criteria, TOP_ACTIVE_LIMIT);
	}

	@Override
	public Optional<SubredditActivity> getMostActive(Long fromUtcTimestamp, RedditEventType eventType) {
		var criteria = searchByTimestamp(fromUtcTimestamp)
				.and(EVENT_TYPE).is(eventType);
		return getAggregates(criteria, SINGLE_DOCUMENT_LIMIT).stream()
				.findFirst();
	}

	private Criteria searchByTimestamp(Long fromUtcTimestamp) {
		return Criteria.where(CREATE_TIMESTAMP_UTC).gt(fromUtcTimestamp);
	}

	private List<SubredditActivity> getAggregates(Criteria criteria, int maxResults) {
		var match = Aggregation.match(criteria);
		var group = groupByName();
		var projection = projectNameAndCount();
		var sort = sortByNumberOfEvents();
		var limit = Aggregation.limit(maxResults);
		var aggregation = Aggregation.newAggregation(match, group, projection, sort, limit);
		return mongoTemplate.aggregate(aggregation, RedditEvent.class, SubredditActivity.class)
				.getMappedResults();
	}

	private SortOperation sortByNumberOfEvents() {
		return Aggregation
				.sort(Sort.Direction.DESC, NUMBER_OF_EVENTS);
	}

	private ProjectionOperation projectNameAndCount() {
		return Aggregation.project(SUBREDDIT_NAME, NUMBER_OF_EVENTS);
	}

	private GroupOperation groupByName() {
		return Aggregation.group(SUBREDDIT_NAME)
				.last(SUBREDDIT_NAME).as(SUBREDDIT_NAME)
				.count().as(NUMBER_OF_EVENTS);
	}
}
