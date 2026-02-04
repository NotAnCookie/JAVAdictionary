package pl.edu.dictionary.api;

import androidx.annotation.NonNull;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
	private static final String BASE_URL = "http://10.0.2.2:8080/";
	private static Retrofit retrofit = null;
	
	@NonNull
	public static DictionaryApi getDictionaryClient() {
		initRetrofit();
		return retrofit.create(DictionaryApi.class);
	}
	
	@NonNull
	public static AutocompleteApi getAutocompleteClient() {
		initRetrofit();
		return retrofit.create(AutocompleteApi.class);
	}
	
	private static void initRetrofit() {
		if (retrofit != null) return;
		// Add the Interceptor to the OkHttpClient
		OkHttpClient client = new OkHttpClient.Builder()
				.addInterceptor(new MockInterceptor()) // remove this line to use the real API
				.build();
		
		retrofit = new Retrofit.Builder()
				.baseUrl(BASE_URL)
				.client(client)
				.addConverterFactory(GsonConverterFactory.create())
				.addCallAdapterFactory(RxJava3CallAdapterFactory.create())
				.build();
	}
}

