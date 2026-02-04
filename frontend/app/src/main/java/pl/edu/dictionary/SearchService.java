package pl.edu.dictionary;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.CheckReturnValue;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.edu.dictionary.api.ApiClient;
import pl.edu.dictionary.models.DictionaryProvider;
import pl.edu.dictionary.models.Language;
import pl.edu.dictionary.models.WordDefinition;
import retrofit2.HttpException;

public class SearchService {
	
	private final SearchHistoryManager historyManager;
	private final ArrayAdapter<String> autoCompleteAdapter;
	
	public SearchService(Context context) {
		this.historyManager = new SearchHistoryManager(context);
		var history = new ArrayList<>(historyManager.getHistory());
		this.autoCompleteAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, history);
	}
	
	public SearchHistoryManager getHistoryManager() { return historyManager; }
	public ArrayAdapter<String> getAutoCompleteAdapter() { return autoCompleteAdapter; }
	
	
	@Nullable
	@CheckReturnValue
	public Disposable performSearch(String word,
	                                DictionaryProvider provider,
	                                Language language,
	                                Consumer<List<WordDefinition>> onSuccess,
	                                Consumer<Throwable> onError) {
		return performSearch(word, provider, language, null, null, onSuccess, onError);
	}
	
	@Nullable
	@CheckReturnValue
	public Disposable performSearch(String word,
	                                DictionaryProvider provider,
	                                Language language,
	                                @Nullable Consumer<Disposable> doOnSubscribe,
	                                @Nullable Action doFinally,
	                                Consumer<List<WordDefinition>> onSuccess,
	                                Consumer<Throwable> onError) {
		if (word.isEmpty()) return null;
		if (provider == DictionaryProvider.ALL) provider = null;
		String providerString = provider != null ? provider.getId() : null;
		String languageString = language != null ? language.getCode() : null;
		
		Observable<List<WordDefinition>> observable = ApiClient.getDictionaryClient().getDefinitions(word, providerString, languageString)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread());
		if (doOnSubscribe != null)
			observable = observable.doOnSubscribe(doOnSubscribe);
		if (doFinally != null)
			observable = observable.doFinally(doFinally);
		
		return observable.subscribe(
				definitions -> {
					if (definitions != null && !definitions.isEmpty()) {
						saveSearch(word);
						onSuccess.accept(definitions);
					} else {
						onError.accept(new WordNotFoundException("No definitions found for: " + word));
					}
				},
				t -> {
					if (t instanceof HttpException httpException && httpException.code() == 404) {
						onError.accept(new WordNotFoundException("Word not found: " + word));
					}
					else
						onError.accept(t);
				}
		);
	}
	
	public void syncHistory() {
		var history = new ArrayList<>(historyManager.getHistory());
		autoCompleteAdapter.clear();
		autoCompleteAdapter.addAll(history);
		autoCompleteAdapter.notifyDataSetChanged();
	}
	
	private void saveSearch(String word) {
		historyManager.saveSearch(word);
		autoCompleteAdapter.remove(word); // Ensure only one entry is displayed for each word
		autoCompleteAdapter.add(word);
		autoCompleteAdapter.notifyDataSetChanged();
	}
	
	public static class WordNotFoundException extends RuntimeException {
		public WordNotFoundException(String message) {
			super(message);
		}
	}
}
