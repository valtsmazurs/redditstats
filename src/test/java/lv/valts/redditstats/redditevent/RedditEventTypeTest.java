package lv.valts.redditstats.redditevent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.Test;

import lv.valts.redditstats.redditevent.RedditEventType.RedditEventTypeNotFound;

class RedditEventTypeTest {
	private static final String SUBMISSION_CODE = "rs";
	private static final String COMMENT_CODE = "rc";
	private static final String KEEPALIVE_CODE = "keepalive";
	private static final String UNSUPPORTED_CODE = "unsupported";

	@Test
	void createsSubmission() {
		var eventType = RedditEventType.byEventCode(SUBMISSION_CODE);

		assertThat(eventType).isSameAs(RedditEventType.SUBMISSION);
	}

	@Test
	void createsComment() {
		var eventType = RedditEventType.byEventCode(COMMENT_CODE);

		assertThat(eventType).isSameAs(RedditEventType.COMMENT);
	}

	@Test
	void createsKeepalive() {
		var eventType = RedditEventType.byEventCode(KEEPALIVE_CODE);

		assertThat(eventType).isSameAs(RedditEventType.KEEPALIVE);
	}

	@Test
	void failsWhenUnknownCode() {
		assertThatExceptionOfType(RedditEventTypeNotFound.class)
				.isThrownBy(() -> RedditEventType.byEventCode(UNSUPPORTED_CODE));
	}
}