package pl.edu.dictionary;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SearchHistoryManager {
	private static final String PREF_NAME = "search_history_prefs";
	private static final String KEY_HISTORY = "history_list";
	private final SharedPreferences sharedPreferences;
	
	private static final int MAX_HISTORY_SIZE = 50;
	private static final String delimiter = "#";
	
	public SearchHistoryManager(Context context) {
		this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
	}
	
	public void saveSearch(String word) {
		if (word == null || word.trim().isEmpty()) return;
		// ensure that delimiter is not in the word
		word = word.replace(delimiter, "");
		
		List<String> history = new ArrayList<>(getHistory());
		// Remove if exists to move it to the top
		history.remove(word);
		history.add(0, word);
		
		if (history.size() > MAX_HISTORY_SIZE) {
			history = history.subList(0, MAX_HISTORY_SIZE);
		}
		
		saveHistory(history);
	}
	
	private void saveHistory(List<String> history) {
		String serialised = TextUtils.join(delimiter, history);
		sharedPreferences.edit()
				.putString(KEY_HISTORY, serialised)
				.apply();
	}
	
	public List<String> getHistory() {
		String serialised = sharedPreferences.getString(KEY_HISTORY, "");
		if (serialised.isEmpty()) return Collections.emptyList();
		String[] split = serialised.split(delimiter);
		return Arrays.asList(split);
	}
	
	public void clearHistory() {
		sharedPreferences.edit().remove(KEY_HISTORY).apply();
	}
	
	public boolean removeSearch(String word) {
		List<String> history = new ArrayList<>(getHistory());
		if (history.remove(word)) {
			saveHistory(history);
			return true;
		}
		return false;
	}
	
}