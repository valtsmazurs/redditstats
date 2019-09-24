package lv.valts.redditstats.gathering;

import java.lang.invoke.MethodHandles;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.sse.SseEventSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
class PushShiftDataGatherer implements Gatherer {
	private static final String URL = "http://stream.pushshift.io";
	private static final int WAIT_MILLISECONDS = 100;

	private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private volatile boolean shouldWork = true;

	private final EventSaver eventSaver;

	@Autowired
	PushShiftDataGatherer(EventSaver eventSaver) {
		this.eventSaver = eventSaver;
	}

	@Override
	public void start() {
		logger.info("Pushshift.io data gatherer started");
		listenToSse();
		logger.info("Pushshift.io data gatherer stopped");
	}

	@Override
	public void stop() {
		shouldWork = false;
	}

	private void listenToSse() {
		var client = ClientBuilder.newClient();
		var target = client.target(URL);
		try (var source = SseEventSource.target(target).build()) {
			source.register(eventSaver::saveEvent, this::logError);
			source.open();
			loopUntilStopped();
		}
	}

	private void loopUntilStopped() {
		while (shouldWork) {
			try {
				Thread.sleep(WAIT_MILLISECONDS);
			} catch (InterruptedException e) {
				throw new GathererInterrupted(e);
			}
		}
	}

	private void logError(Throwable exception) {
		logger.error("Error when processing stream: ", exception);
	}

	private static class GathererInterrupted extends RuntimeException {
		GathererInterrupted(Throwable cause) {
			super(cause);
		}
	}
}
