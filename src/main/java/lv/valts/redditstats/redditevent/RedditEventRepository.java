package lv.valts.redditstats.redditevent;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RedditEventRepository extends MongoRepository<RedditEvent, String> {

	@Query(value = "{'createTimestampUtc' : { $gt: ?0 }, 'eventType' : ?1}", count = true)
	Long totalActivity(Long fromUtcTimestamp, RedditEventType eventType);
}
