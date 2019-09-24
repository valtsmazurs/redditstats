package lv.valts.redditstats.gathering;

import static lv.valts.redditstats.redditevent.RedditEventType.COMMENT;
import static lv.valts.redditstats.redditevent.RedditEventType.KEEPALIVE;
import static lv.valts.redditstats.redditevent.RedditEventType.SUBMISSION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import javax.ws.rs.sse.InboundSseEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import lv.valts.redditstats.redditevent.RedditEvent;
import lv.valts.redditstats.redditevent.RedditEventRepository;
import lv.valts.redditstats.redditevent.RedditEventType;

@ExtendWith(MockitoExtension.class)
class EventSaverTest {
	private static final String SUBREDDIT_NAME = "some name";
	private static final long UTC_TIMESTAMP = 12L;
	private static final ImmutableEventData EVENT_DATA = ImmutableEventData.builder()
			.subreddit(SUBREDDIT_NAME)
			.createdUtc(UTC_TIMESTAMP)
			.build();
	private static final Gson GSON = new GsonBuilder()
			.registerTypeAdapterFactory(new GsonAdaptersEventData())
			.create();
	private static final String EVENT_DATA_JSON = GSON.toJson(EVENT_DATA);

	@Mock
	private RedditEventRepository redditEventRepository;
	@Mock
	private InboundSseEvent sseEvent;
	@InjectMocks
	EventSaver eventSaver;
	@Captor
	ArgumentCaptor<RedditEvent> redditEventCaptor;

	@Test
	void savesSubmissionEvent() {
		given(sseEvent.getName()).willReturn(SUBMISSION.getEventCode());
		given(sseEvent.readData()).willReturn(EVENT_DATA_JSON);

		eventSaver.saveEvent(sseEvent);

		thenShouldInsertEvent(SUBMISSION);
	}

	@Test
	void savesCommentEvent() {
		given(sseEvent.getName()).willReturn(COMMENT.getEventCode());
		given(sseEvent.readData()).willReturn(EVENT_DATA_JSON);

		eventSaver.saveEvent(sseEvent);

		thenShouldInsertEvent(COMMENT);
	}

	@Test
	void skipsSavingEvent() {
		given(sseEvent.getName()).willReturn(KEEPALIVE.getEventCode());

		eventSaver.saveEvent(sseEvent);

		then(redditEventRepository).shouldHaveZeroInteractions();
	}

	private void thenShouldInsertEvent(RedditEventType eventType) {
		then(redditEventRepository).should().insert(redditEventCaptor.capture());
		var redditEvent = redditEventCaptor.getValue();
		assertThat(redditEvent.getId()).isNotBlank();
		assertThat(redditEvent.getEventType()).isSameAs(eventType);
		assertThat(redditEvent.getSubredditName()).isEqualTo(SUBREDDIT_NAME);
		assertThat(redditEvent.getCreateTimestampUtc()).isEqualTo(UTC_TIMESTAMP);
	}
}