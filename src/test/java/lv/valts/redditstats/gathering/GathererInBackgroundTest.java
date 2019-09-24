package lv.valts.redditstats.gathering;

import static org.mockito.BDDMockito.then;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.event.ContextClosedEvent;

@ExtendWith(MockitoExtension.class)
class GathererInBackgroundTest {
	@Mock
	private Gatherer gatherer;
	@Mock
	private ContextClosedEvent contextClosedEvent;
	@InjectMocks
	private GathererInBackground gathererInBackground;

	@Test
	void stopsGatherer() throws InterruptedException {
		gathererInBackground.stop(contextClosedEvent);

		then(gatherer).should().stop();
	}
}