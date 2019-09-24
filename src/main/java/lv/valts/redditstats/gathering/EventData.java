package lv.valts.redditstats.gathering;

import org.immutables.gson.Gson;
import org.immutables.value.Value;
import com.google.gson.annotations.SerializedName;

@Value.Immutable
@Gson.TypeAdapters
interface EventData {
	@SerializedName("created_utc")
	Long getCreatedUtc();

	@SerializedName("subreddit")
	String getSubreddit();
}
