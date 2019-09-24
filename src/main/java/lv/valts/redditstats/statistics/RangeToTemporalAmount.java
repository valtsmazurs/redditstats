package lv.valts.redditstats.statistics;

import static lv.valts.redditstats.statistics.TimeRange.FIVE_MINUTES;
import static lv.valts.redditstats.statistics.TimeRange.ONE_DAY;
import static lv.valts.redditstats.statistics.TimeRange.ONE_HOUR;
import static lv.valts.redditstats.statistics.TimeRange.ONE_MINUTE;
import static lv.valts.redditstats.statistics.TimeRange.UNLIMITED;

import java.time.Duration;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.Map;

class RangeToTemporalAmount {
	private static final Duration ONE_MINUTE_DURATION = Duration.ofMinutes(1);
	private static final Duration FIVE_MINUTE_DURATION = Duration.ofMinutes(5);
	private static final Duration ONE_HOUR_DURATION = Duration.ofHours(1);
	private static final Period ONE_DAY_PERIOD = Period.ofDays(1);
	private static final Period MANY_YEARS_PERIOD = Period.ofYears(100);
	private static final Map<TimeRange, TemporalAmount> MAPPING = Map.of(
			ONE_MINUTE, ONE_MINUTE_DURATION,
			FIVE_MINUTES, FIVE_MINUTE_DURATION,
			ONE_HOUR, ONE_HOUR_DURATION,
			ONE_DAY, ONE_DAY_PERIOD,
			UNLIMITED, MANY_YEARS_PERIOD
	);

	private final TimeRange range;

	private RangeToTemporalAmount(TimeRange range) {
		this.range = range;
	}

	static RangeToTemporalAmount of(TimeRange range) {
		return new RangeToTemporalAmount(range);
	}

	TemporalAmount transform() {
		return MAPPING.get(range);
	}
}
