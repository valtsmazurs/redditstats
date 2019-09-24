package lv.valts.redditstats.gathering;

import static lv.valts.redditstats.redditevent.RedditEventType.COMMENT;
import static lv.valts.redditstats.redditevent.RedditEventType.SUBMISSION;

import java.lang.invoke.MethodHandles;
import java.util.Set;
import java.util.UUID;
import javax.ws.rs.sse.InboundSseEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lv.valts.redditstats.redditevent.ImmutableRedditEvent;
import lv.valts.redditstats.redditevent.RedditEvent;
import lv.valts.redditstats.redditevent.RedditEventRepository;
import lv.valts.redditstats.redditevent.RedditEventType;

@Service
class EventSaver {
	private static final Set<RedditEventType> EVENTS_TO_SAVE = Set.of(SUBMISSION, COMMENT);
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapterFactory(new GsonAdaptersEventData())
			.create();

	private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final RedditEventRepository redditEventRepository;

	@Autowired
	EventSaver(RedditEventRepository redditEventRepository) {
		this.redditEventRepository = redditEventRepository;
	}

	void saveEvent(InboundSseEvent event) {
		if (shouldSaveEvent(event)) {
			logger.trace("Received event: {} (id: {})", event.getName(), event.getId());
			var redditEvent = createRedditEvent(event);
			redditEventRepository.insert(redditEvent);
			logger.trace("Inserted event: {}", redditEvent);
		}
	}

	private boolean shouldSaveEvent(InboundSseEvent event) {
		var eventType = getRedditEventType(event);
		return EVENTS_TO_SAVE.contains(eventType);
	}

	private RedditEventType getRedditEventType(InboundSseEvent event) {
		return RedditEventType.byEventCode(event.getName());
	}

	private RedditEvent createRedditEvent(InboundSseEvent event) {
		var eventData = getEventData(event);
		var eventType = getRedditEventType(event);
		return ImmutableRedditEvent.builder()
				.id(UUID.randomUUID().toString())
				.eventType(eventType)
				.subredditName(eventData.getSubreddit())
				.createTimestampUtc(eventData.getCreatedUtc())
				.build();
	}

	private EventData getEventData(InboundSseEvent event) {
		var dataAsText = event.readData();
		return GSON.fromJson(dataAsText, EventData.class);
	}
}
