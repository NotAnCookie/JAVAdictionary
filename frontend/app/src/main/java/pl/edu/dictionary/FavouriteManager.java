package pl.edu.dictionary;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FavouriteManager {
	private static final String PREF_NAME = "favourites_prefs";
	private static final String KEY_FAVOURITES = "favourites_list";
	private final SharedPreferences sharedPreferences;
	
	private static final String delimiter = "#";
	
	public FavouriteManager(Context context) {
		this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
	}
	
	public void saveFavourite(String word) {
		if (word == null || word.trim().isEmpty()) return;
		// ensure that delimiter is not in the word
		word = word.replace(delimiter, "");
		
		List<String> favourites = new ArrayList<>(getFavourites());
		// Remove if exists to move it to the top
		favourites.remove(word);
		favourites.add(0, word);
		
		saveFavourites(favourites);
	}
	
	private void saveFavourites(List<String> favourites) {
		String serialised = TextUtils.join(delimiter, favourites);
		sharedPreferences.edit()
				.putString(KEY_FAVOURITES, serialised)
				.apply();
	}
	
	public List<String> getFavourites() {
		String serialised = sharedPreferences.getString(KEY_FAVOURITES, "");
		if (serialised.isEmpty()) return Collections.emptyList();
		String[] split = serialised.split(delimiter);
		return Arrays.asList(split);
	}
	
	public boolean removeFavourite(String word) {
		List<String> favourites = new ArrayList<>(getFavourites());
		if (favourites.remove(word)) {
			saveFavourites(favourites);
			return true;
		}
		return false;
	}
	
}