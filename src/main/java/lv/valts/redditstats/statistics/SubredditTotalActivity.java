package lv.valts.redditstats.statistics;

import org.immutables.gson.Gson;
import org.immutables.value.Value;

@Value.Immutable
@Gson.TypeAdapters
public interface SubredditTotalActivity {
	String getSubredditName();

	Long getNumberOfCommentsAndSubmissions();
}
