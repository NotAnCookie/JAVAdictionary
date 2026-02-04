package pl.edu.dictionary.api;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MockInterceptor implements Interceptor {
	
	private static final boolean SLEEP = true;
	private static final boolean LOG_RESPONSE = false;
	private static final boolean MOCK_RESPONSE = true;
	
	@NonNull
	@Override
	public Response intercept(Chain chain) throws IOException {
		String uri = chain.request().url().uri().toString();
		Log.d("MockInterceptor", "uri: " + uri);
		if (SLEEP) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
		if (LOG_RESPONSE || !MOCK_RESPONSE) {
			var response = chain.proceed(chain.request());
			if (LOG_RESPONSE)
				Log.d("MockInterceptor", "responseString: " + response.body().string());
			if (!MOCK_RESPONSE)
				return response;
		}
		
		// mock
		if (uri.contains("dictionary/no")) {
			return new Response.Builder()
					.code(404)
					.message("Not Found")
					.request(chain.request())
					.protocol(Protocol.HTTP_1_0)
					.body(ResponseBody.create("".getBytes(), MediaType.parse("application/json")))
					.addHeader("content-type", "application/json")
					.build();
		}
		return new Response.Builder()
				.code(200)
				.message("OK")
				.request(chain.request())
				.protocol(Protocol.HTTP_1_0)
				.body(ResponseBody.create(getResponseString(uri).getBytes(), MediaType.parse("application/json")))
				.addHeader("content-type", "application/json")
				.build();
	}
	
	@NonNull
	private static String getResponseString(String uri) {
		String responseString = "";
		
		// 1. Mocking getProviders()
		if (uri.contains("providers")) {
			responseString = "[{\"id\":\"Google\", \"languageAware\":true, \"supportedLanguages\":[\"EN\", \"ES\"]}," +
					"{\"id\":\"Wikisłownik\", \"languageAware\":false, \"supportedLanguages\":[\"EN\"]}]";
		}
		// 2. Mocking getDefinitions(word, provider)
		else if (uri.contains("dictionary")) {
			if (uri.contains("?provider=Google"))
				responseString = "{" +
								"\"word\": \"java\"," +
								"\"definition\": \"A high-level programming language\"," +
								"\"synonyms\": [\"Coffee\", \"OOP\"]," +
								"\"provider\": \"Google\"" +
								"}";
			else
				responseString = "{" +
								"\"word\": \"java\"," +
								"\"definition\": \"An island in Indonesia\"," +
								"\"synonyms\": [\"Coffee\", \"no\"]," +
								"\"provider\": \"Wikisłownik\"" +
								"}";
		}
		else if (uri.contains("autocomplete")) {
			responseString = "[\"java\", \"javascript\"]";
		}
		return responseString;
	}
	
}

