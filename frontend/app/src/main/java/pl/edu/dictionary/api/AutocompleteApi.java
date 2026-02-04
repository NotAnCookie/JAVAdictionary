package pl.edu.dictionary.api;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface AutocompleteApi {
	@GET("autocomplete")
	Observable<List<String>> getAutocompleteSuggestions(@Query("q") String query);
}
