package lv.valts.redditstats.statistics;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.time.Period;
import org.junit.jupiter.api.Test;

class RangeToTemporalAmountTest {
	private static final Duration ONE_MINUTE_DURATION = Duration.ofMinutes(1);
	private static final Duration FIVE_MINUTE_DURATION = Duration.ofMinutes(5);
	private static final Duration ONE_HOUR_DURATION = Duration.ofHours(1);
	private static final Period ONE_DAY_PERIOD = Period.ofDays(1);
	private static final Period MANY_YEARS_PERIOD = Period.ofYears(100);

	@Test
	void transformsToMinute() {
		var temporalAmount = RangeToTemporalAmount.of(TimeRange.ONE_MINUTE).transform();

		assertThat(temporalAmount).isEqualTo(ONE_MINUTE_DURATION);
	}

	@Test
	void transformsTo5Minutes() {
		var temporalAmount = RangeToTemporalAmount.of(TimeRange.FIVE_MINUTES).transform();

		assertThat(temporalAmount).isEqualTo(FIVE_MINUTE_DURATION);
	}

	@Test
	void transformsToHour() {
		var temporalAmount = RangeToTemporalAmount.of(TimeRange.ONE_HOUR).transform();

		assertThat(temporalAmount).isEqualTo(ONE_HOUR_DURATION);
	}

	@Test
	void transformsToDay() {
		var temporalAmount = RangeToTemporalAmount.of(TimeRange.ONE_DAY).transform();

		assertThat(temporalAmount).isEqualTo(ONE_DAY_PERIOD);
	}

	@Test
	void transformsToReasonablyBigNumberOfYears() {
		var temporalAmount = RangeToTemporalAmount.of(TimeRange.UNLIMITED).transform();

		assertThat(temporalAmount).isEqualTo(MANY_YEARS_PERIOD);
	}
}