package pl.edu.dictionary.api;

import androidx.annotation.Nullable;

import java.util.List;

import pl.edu.dictionary.models.WordDefinition;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface DictionaryApi {
	@GET("dictionary/{word}")
	Call<List<WordDefinition>> getDefinitions(
			@Path("word") String word,
			@Nullable @Query("provider") String provider
	);
	
	@GET("dictionary/providers")
	Call<List<String>> getProviders();
	
	
}
