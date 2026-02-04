package pl.edu.dictionary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.edu.dictionary.api.ApiClient;
import pl.edu.dictionary.models.DictionaryProvider;
import pl.edu.dictionary.models.Language;
import retrofit2.HttpException;

public class MainActivity extends AppCompatActivity {
	
	private AutoCompleteTextView searchEditText;
	private Spinner providerSpinner;
	private Spinner languageSpinner;
	private TextView providerTextView;
	private TextView languageTextView;
	private ProgressBar progressBar;
	private TextView errorTextView;
	private TextView wordNotFoundTextView;
	private ListView autocompleteListView;
	
	private SearchHistoryManager historyManager;
	private ArrayAdapter<String> autoCompleteAdapter;
	
	private List<DictionaryProvider> dictionaryProviders;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		setSupportActionBar(findViewById(R.id.toolbar));
		
		historyManager = new SearchHistoryManager(this);
		
		searchEditText = findViewById(R.id.searchEditText);
		providerSpinner = findViewById(R.id.providerSpinner);
		languageSpinner = findViewById(R.id.languageSpinner);
		providerTextView = findViewById(R.id.providerTextView);
		languageTextView = findViewById(R.id.languageTextView);
		errorTextView = findViewById(R.id.errorTextView);
		wordNotFoundTextView = findViewById(R.id.wordNotFoundTextView);
		autocompleteListView = findViewById(R.id.autocompleteListView);
		progressBar = findViewById(R.id.progressBar);
		searchEditText.setOnEditorActionListener((v, actionId, event) -> {
			performSearch();
			// Hide the keyboard
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(searchEditText.getWindowToken(), 0);
			return true;
		});
		
		providerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				DictionaryProvider selectedProvider = (DictionaryProvider) parent.getItemAtPosition(position);
				if (selectedProvider == null) return;
				languageSpinner.setAdapter(new ArrayAdapter<>(
						MainActivity.this,
						android.R.layout.simple_spinner_item,
						selectedProvider.isLanguageAware() ? selectedProvider.getSupportedLanguages().toArray() : new Object[0]
				));
				setLanguagesVisibility(selectedProvider.isLanguageAware());
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				Log.d("MainActivity", "Nothing selected");
			}
		});
		
		autocompleteListView.setOnItemClickListener((parent, view, position, id) -> {
			String selectedWord = (String) parent.getItemAtPosition(position);
			searchEditText.setText(selectedWord);
			performSearch();
		});
		
		var history = new ArrayList<>(historyManager.getHistory());
		autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, history);
		searchEditText.setAdapter(autoCompleteAdapter);
		
		updateProviderSpinner(Collections.emptyList());
		updateProviders();
		
	}
	
	@SuppressWarnings("ResultOfMethodCallIgnored")
	@SuppressLint("CheckResult")
	private void updateProviders() {
		ApiClient.getDictionaryClient().getProviders()
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.doOnSubscribe(disposable -> progressBar.setVisibility(View.VISIBLE))
				.doFinally(() -> progressBar.setVisibility(View.GONE))
				.subscribe(
						this::updateProviderSpinner,
						t -> {
							dictionaryProviders = Collections.emptyList();
							Log.e("MainActivity", "Provider load error", t);
							errorTextView.setText("Error: Connection failed.");
						}
				);
	}
	
	private void updateProviderSpinner(List<DictionaryProvider> providers) {
		dictionaryProviders = new ArrayList<>(providers);
		dictionaryProviders.add(0, DictionaryProvider.ALL);
		providerSpinner.setSelection(0);
		ArrayAdapter<DictionaryProvider> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, dictionaryProviders);
		providerSpinner.setAdapter(adapter);
		setProvidersVisibility(!providers.isEmpty());
	}
	
	@SuppressWarnings("ResultOfMethodCallIgnored")
	@SuppressLint("CheckResult")
	private void performSearch() {
		String word = searchEditText.getText().toString();
		if (word.isEmpty()) return;
		
		DictionaryProvider provider = (DictionaryProvider) providerSpinner.getSelectedItem();
		if (provider == DictionaryProvider.ALL) provider = null;
		String providerString = provider != null ? provider.getId() : null;
		
		Language language = (Language) languageSpinner.getSelectedItem();
		String languageString = language != null ? language.getCode() : null;
		
		progressBar.setVisibility(View.VISIBLE);
		
		ApiClient.getDictionaryClient().getDefinitions(word, providerString, languageString)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						definitions -> {
							if (definitions != null && !definitions.isEmpty()) {
								saveSearch(word);
								DefinitionActivity.launchActivity(this, definitions);
							} else {
								errorTextView.setText("No definitions found for: " + word);
							}
							progressBar.setVisibility(View.GONE);
						},
						t -> {
							if (t instanceof HttpException httpException && httpException.code() == 404) {
								autoComplete(word);
								return;
							}
							displayError(word, t);
						}
				);
	}
	
	private void saveSearch(String word) {
		historyManager.saveSearch(word);
		autoCompleteAdapter.remove(word); // Ensure only one entry is displayed for each word
		autoCompleteAdapter.add(word);
		autoCompleteAdapter.notifyDataSetChanged();
	}
	
	@SuppressWarnings("ResultOfMethodCallIgnored")
	@SuppressLint("CheckResult")
	private void autoComplete(String query) {
		ApiClient.getAutocompleteClient().getAutocompleteSuggestions(query)
				.subscribeOn(Schedulers.io())
				.observeOn(AndroidSchedulers.mainThread())
				.subscribe(
						suggestions -> {
							showAutocompleteSuggestions(suggestions);
							progressBar.setVisibility(View.GONE);
						},
						t -> displayError(query, t)
				);
	}
	
	private void showAutocompleteSuggestions(List<String> suggestions) {
		if (suggestions.isEmpty()) {
			wordNotFoundTextView.setVisibility(View.VISIBLE);
			autocompleteListView.setVisibility(View.GONE);
			wordNotFoundTextView.setText("Word '" + searchEditText.getText() + "' not found.");
		} else {
			wordNotFoundTextView.setVisibility(View.VISIBLE);
			autocompleteListView.setVisibility(View.VISIBLE);
			wordNotFoundTextView.setText("Word '" + searchEditText.getText() + "' not found.\nDid You mean:");
			autocompleteListView.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, suggestions));
		}
	}
	
	private void displayError(String word, Throwable t) {
		Log.e("MainActivity", "Word search error", t);
		if (t instanceof HttpException httpException) {
			
			if (httpException.code() >= 500)
				errorTextView.setText("Internal server error.");
			else
				errorTextView.setText("Error: " + httpException.message());
		} else {
			errorTextView.setText("Error: Connection failed.");
		}
		progressBar.setVisibility(View.GONE);
	}
	
	private void setProvidersVisibility(boolean visible) {
		providerTextView.setVisibility(visible ? View.VISIBLE : View.GONE);
		providerSpinner.setVisibility(visible ? View.VISIBLE : View.GONE);
		if (!visible) {
			languageTextView.setVisibility(View.GONE);
			languageSpinner.setVisibility(View.GONE);
		}
	}
	
	private void setLanguagesVisibility(boolean visible) {
		languageTextView.setVisibility(visible ? View.VISIBLE : View.GONE);
		languageSpinner.setVisibility(visible ? View.VISIBLE : View.GONE);
	}
	
	private final ActivityResultLauncher<Intent> historyLauncher =
			registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
				if (result.getResultCode() == RESULT_OK && result.getData() != null) {
					// Automatically search when selected from history
					String word = result.getData().getStringExtra("selected_word");
					searchEditText.setText(word);
					performSearch();
				}
			});
	
	private void openHistory() {
		Intent intent = new Intent(this, HistoryActivity.class);
		historyLauncher.launch(intent);
	}
	
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_history) {
			openHistory();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
	
}
