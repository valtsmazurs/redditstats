package lv.valts.redditstats.redditevent;

import java.util.Arrays;

public enum RedditEventType {
	SUBMISSION("rs"), COMMENT("rc"), KEEPALIVE("keepalive");

	private final String eventCode;

	RedditEventType(String eventCode) {
		this.eventCode = eventCode;
	}

	public static RedditEventType byEventCode(String eventCode) {
		return Arrays.stream(RedditEventType.values())
				.filter(item -> item.eventCode.equals(eventCode))
				.findFirst()
				.orElseThrow(() -> new RedditEventTypeNotFound(eventCode));
	}

	public String getEventCode() {
		return eventCode;
	}

	static class RedditEventTypeNotFound extends RuntimeException {
		RedditEventTypeNotFound(String eventCode) {
			super("RedditEventType not found by code: " + eventCode);
		}
	}
}
