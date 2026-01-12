package pl.edu.dictionary.api;

import android.util.Log;

import androidx.annotation.NonNull;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class MockInterceptor implements Interceptor {
	
	@NonNull
	@Override
	public Response intercept(Chain chain) {
		String uri = chain.request().url().uri().toString();
		Log.d("MockInterceptor", "uri: " + uri);
		
		String responseString = getResponseString(uri);
		
		return new Response.Builder()
				.code(200)
				.message("OK")
				.request(chain.request())
				.protocol(Protocol.HTTP_1_0)
				.body(ResponseBody.create(responseString.getBytes(), MediaType.parse("application/json")))
				.addHeader("content-type", "application/json")
				.build();
	}
	
	@NonNull
	private static String getResponseString(String uri) {
		String responseString = "";
		
		// 1. Mocking getProviders()
		if (uri.contains("providers")) {
			responseString = "[\"All\", \"Google\", \"Oxford\", \"Wikisłownik\"]";
		}
		// 2. Mocking getDefinitions(word, provider)
		else if (uri.contains("dictionary")) {
			responseString = "[" +
					"{" +
					"\"word\": \"java\"," +
					"\"definition\": \"A high-level programming language\"," +
					"\"synonyms\": [\"Coffee\", \"OOP\"]" +
					"}," +
					"{" +
					"\"word\": \"java\"," +
					"\"definition\": \"An island in Indonesia\"," +
					"\"synonyms\": [\"Coffee\"]" +
					"}" +
					"]";
		}
		return responseString;
	}
	
}

