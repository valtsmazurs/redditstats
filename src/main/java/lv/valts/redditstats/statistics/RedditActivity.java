package lv.valts.redditstats.statistics;

import org.immutables.value.Value;

@Value.Immutable
public interface RedditActivity {
	Integer getNumberOfSubmissions();

	Integer getNumberOfComments();
}
