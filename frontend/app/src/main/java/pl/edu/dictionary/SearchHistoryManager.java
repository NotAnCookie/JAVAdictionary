package pl.edu.dictionary;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SearchHistoryManager {
	private static final String PREF_NAME = "search_history_prefs";
	private static final String KEY_HISTORY = "history_list";
	private final SharedPreferences sharedPreferences;
	
	private static final int MAX_HISTORY_SIZE = 50;
	
	public SearchHistoryManager(Context context) {
		this.sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
	}
	
	public void saveSearch(String word) {
		if (word == null || word.trim().isEmpty()) return;
		
		List<String> history = getHistory();
		// Remove if exists to move it to the top
		history.remove(word);
		history.add(0, word);
		
		if (history.size() > MAX_HISTORY_SIZE) {
			history = history.subList(0, MAX_HISTORY_SIZE);
		}
		
		sharedPreferences.edit()
				.putStringSet(KEY_HISTORY, new HashSet<>(history))
				.apply();
	}
	
	public List<String> getHistory() {
		Set<String> set = sharedPreferences.getStringSet(KEY_HISTORY, new HashSet<>());
		return new ArrayList<>(set);
	}
	
	public List<String> getHistoryContaining(String query) {
	    List<String> history = getHistory();
	    return history.stream().filter(s -> s.contains(query)).collect(Collectors.toList());
	}
	
	public void clearHistory() {
		sharedPreferences.edit().remove(KEY_HISTORY).apply();
	}
	
	public boolean removeSearch(String word) {
		List<String> history = getHistory();
		if (history.remove(word)) {
			sharedPreferences.edit()
					.putStringSet(KEY_HISTORY, new HashSet<>(history))
					.apply();
			return true;
		}
		return false;
	}
	
}