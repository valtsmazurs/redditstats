package lv.valts.redditstats.gathering;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
class GathererInBackground {
	private static final int TERMINATION_TIMEOUT = 1;
	private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final Gatherer gatherer;
	private final ExecutorService executor = Executors.newSingleThreadExecutor();

	@Autowired
	GathererInBackground(Gatherer gatherer) {
		this.gatherer = gatherer;
	}

	@EventListener
	public void start(ContextRefreshedEvent event) {
		logger.info("Starting gatherer");
		executor.submit(gatherer::start);
	}

	@EventListener
	public void stop(ContextClosedEvent event) throws InterruptedException {
		logger.info("Stopping gatherer");
		gatherer.stop();
		executor.awaitTermination(TERMINATION_TIMEOUT, TimeUnit.SECONDS);
	}
}
