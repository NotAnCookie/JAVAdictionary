package pl.edu.dictionary.api;

import androidx.annotation.Nullable;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import pl.edu.dictionary.models.WordDefinition;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DictionaryApi {
	@GET("dictionary/{word}")
	Observable<WordDefinition> getDefinition(
			@Path("word") String word,
			@Nullable @Query("provider") String provider
	);
	
	@GET("dictionary/providers")
	Observable<List<String>> getProviders();
	
	default Observable<List<WordDefinition>> getDefinitions(String word, @Nullable String provider) {
		Observable<WordDefinition> observable = getDefinition(word, provider);
		return observable.map(List::of);
	}
}
