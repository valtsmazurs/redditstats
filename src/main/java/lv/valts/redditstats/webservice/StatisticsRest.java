package lv.valts.redditstats.webservice;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lv.valts.redditstats.statistics.RedditActivity;
import lv.valts.redditstats.statistics.RedditStatistics;
import lv.valts.redditstats.statistics.SubredditTotalActivity;
import lv.valts.redditstats.statistics.SubredditWithCommentCount;
import lv.valts.redditstats.statistics.SubredditWithSubmissionCount;
import lv.valts.redditstats.statistics.TimeRange;

@RestController
class StatisticsRest {
	private static final String DEFAULT_TIME_RANGE = "ONE_MINUTE";

	private final RedditStatistics redditStatistics;

	@Autowired
	StatisticsRest(RedditStatistics redditStatistics) {
		this.redditStatistics = redditStatistics;
	}

	@RequestMapping(value = "/activity", method = GET)
	RedditActivity activity(
			@RequestParam(defaultValue = DEFAULT_TIME_RANGE) TimeRange timeRange) {
		return redditStatistics.getActivity(timeRange);
	}

	@RequestMapping(value = "/mostActive/top100", method = GET)
	List<SubredditTotalActivity> top100Active(
			@RequestParam(defaultValue = DEFAULT_TIME_RANGE) TimeRange timeRange) {
		return redditStatistics.getTop100Active(timeRange);
	}

	@RequestMapping(value = "/mostActive/bySubmissions", method = GET)
	Optional<SubredditWithSubmissionCount> mostActiveBySubmissions(
			@RequestParam(defaultValue = DEFAULT_TIME_RANGE) TimeRange timeRange) {
		return redditStatistics.getMostActiveBySubmissionCount(timeRange);
	}

	@RequestMapping(value = "/mostActive/byComments", method = GET)
	Optional<SubredditWithCommentCount> mostActiveByComments(
			@RequestParam(defaultValue = DEFAULT_TIME_RANGE) TimeRange timeRange) {
		return redditStatistics.getMostActiveByCommentCount(timeRange);
	}
}
