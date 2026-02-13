package pl.edu.dictionary;

import android.content.Context;
import android.content.res.Resources;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
	private final FavouriteManager favouriteManager;
	private final ArrayAdapter<String> autoCompleteAdapter;
	private final Resources resources;
	
	private List<DictionaryProvider> dictionaryProviders = new ArrayList<>();
	
	public SearchService(Context context) {
		this.historyManager = new SearchHistoryManager(context);
		this.favouriteManager = new FavouriteManager(context);
		var history = new ArrayList<>(historyManager.getHistory());
		this.autoCompleteAdapter = new ArrayAdapter<>(context, android.R.layout.simple_dropdown_item_1line, history);
		this.resources = context.getResources();
	}
	
	public SearchHistoryManager getHistoryManager() { return historyManager; }
	public ArrayAdapter<String> getAutoCompleteAdapter() { return autoCompleteAdapter; }
	public List<DictionaryProvider> getDictionaryProviders() { return dictionaryProviders; }
	public void setDictionaryProviders(List<DictionaryProvider> dictionaryProviders) { this.dictionaryProviders = new ArrayList<>(dictionaryProviders); }
	
	
	@Nullable
	@CheckReturnValue
	public Disposable performSearch(@NonNull String word,
	                                @Nullable DictionaryProvider provider,
	                                @Nullable Language language,
	                                @NonNull Consumer<List<WordDefinition>> onSuccess,
	                                @NonNull Consumer<Throwable> onError) {
		return performSearch(word, provider, language, null, null, onSuccess, onError);
	}
	
	@Nullable
	@CheckReturnValue
	public Disposable performSearch(@NonNull String word,
	                                @Nullable DictionaryProvider provider,
	                                @Nullable Language language,
	                                @Nullable Consumer<Disposable> doOnSubscribe,
	                                @Nullable Action doFinally,
	                                @NonNull Consumer<List<WordDefinition>> onSuccess,
	                                @NonNull Consumer<Throwable> onError) {
		if (word.isEmpty()) return null;
		if (provider == DictionaryProvider.ALL)
			return performAllSearch(word, dictionaryProviders, doOnSubscribe, doFinally, onSuccess, onError);
		
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
						onError.accept(new WordNotFoundException(resources.getString(R.string.no_definitions_found_format, word)));
					}
				},
				t -> {
					if (t instanceof HttpException httpException && httpException.code() == 404) {
						onError.accept(new WordNotFoundException(resources.getString(R.string.word_not_found_format, word)));
					}
					else
						onError.accept(t);
				}
		);
	}
	
	@Nullable
	public Disposable performAllSearch(@NonNull String word,
									   @NonNull List<DictionaryProvider> dictionaryProviders,
	                                   @Nullable Consumer<Disposable> doOnSubscribe,
	                                   @Nullable Action doFinally,
	                                   @NonNull Consumer<List<WordDefinition>> onSuccess,
	                                   @NonNull Consumer<Throwable> onError) {
		if (dictionaryProviders.isEmpty()) {
			try {
				onError.accept(new RuntimeException(resources.getString(R.string.no_providers_available)));
			} catch (Throwable e) {
				throw new RuntimeException(e);
			}
			return null;
		}
		
		Observable<Result<WordDefinition>> observable = Observable.fromIterable(dictionaryProviders)
				.flatMap(provider ->
						ApiClient.getDictionaryClient().getDefinition(word, provider.getId(), null)
								.subscribeOn(Schedulers.io())
								.map(Result::success)
								.onErrorReturn(Result::error)
				)
				.observeOn(AndroidSchedulers.mainThread());
		if (doOnSubscribe != null)
			observable = observable.doOnSubscribe(doOnSubscribe);
		if (doFinally != null)
			observable = observable.doFinally(doFinally);
		
		return observable
				.toList()
				.subscribe(
						results -> {
							List<WordDefinition> definitions = new ArrayList<>();
							List<Throwable> errors = new ArrayList<>();
							for (Result<WordDefinition> result : results) {
								if (result.isSuccess())
									definitions.add(result.getData());
								else
									errors.add(result.getError());
							}
							
							if (!definitions.isEmpty()) {
								saveSearch(word);
								onSuccess.accept(definitions);
							} else {
								if (errors.stream().allMatch(t -> t instanceof HttpException httpException && httpException.code() == 404))
									onError.accept(new WordNotFoundException(resources.getString(R.string.word_not_found_format, word)));
								else
									onError.accept(new RuntimeException(resources.getString(R.string.connection_error), errors.isEmpty() ? null : errors.get(0)));
							}
						},
						onError);
		
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
	
	public boolean isFavourite(@NonNull String word) {
		return favouriteManager.getFavourites().stream()
				.map( str -> str.toLowerCase(Locale.ROOT))
				.anyMatch(str -> str.equals(word.trim().toLowerCase(Locale.ROOT)));
	}
	
	public void saveFavourite(@NonNull String word) {
		favouriteManager.saveFavourite(word.trim());
	}
	
	public void removeFavourite(@NonNull String word) {
		favouriteManager.removeFavourite(word.trim());
	}
	
	public static class WordNotFoundException extends RuntimeException {
		public WordNotFoundException(String message) {
			super(message);
		}
	}
}
