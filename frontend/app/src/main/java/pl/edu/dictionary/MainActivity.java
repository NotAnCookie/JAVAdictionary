package pl.edu.dictionary;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import pl.edu.dictionary.api.ApiClient;
import pl.edu.dictionary.models.WordDefinition;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
	
	private AutoCompleteTextView searchEditText;
	private Spinner providerSpinner;
	private TextView resultTextView;
	private ProgressBar progressBar;
	
	private SearchHistoryManager historyManager;
	private ArrayAdapter<String> autoCompleteAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		historyManager = new SearchHistoryManager(this);
		
		searchEditText = findViewById(R.id.searchEditText);
		providerSpinner = findViewById(R.id.providerSpinner);
		resultTextView = findViewById(R.id.resultTextView);
		progressBar = findViewById(R.id.progressBar);
		searchEditText.setOnEditorActionListener((v, actionId, event) -> {
			performSearch();
			return true;
		});
		
		autoCompleteAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line);
		searchEditText.setAdapter(autoCompleteAdapter);
		
		updateProviderSpinner(Collections.emptyList());
		updateProviders();
		
	}
	
	private void updateProviders() {
		ApiClient.getClient().getProviders().enqueue(new Callback<>() {
			@Override
			public void onResponse(Call<List<String>> call, Response<List<String>> response) {
				if (response.isSuccessful() && response.body() != null) {
					List<String> providers = response.body();
					updateProviderSpinner(providers);
				}
				else {
					// Handle error
				}
			}
			
			@Override
			public void onFailure(Call<List<String>> call, Throwable t) {
				resultTextView.setText("Error: Connection failed.");
			}
		});
	}
	
	private void updateProviderSpinner(List<String> providers) {
		ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, providers);
		providerSpinner.setAdapter(adapter);
		providerSpinner.setVisibility(providers.isEmpty() ? View.GONE : View.VISIBLE);
		
	}
	
	private void performSearch() {
		String word = searchEditText.getText().toString();
		Object provider = providerSpinner.getSelectedItem();
		if (Objects.toString(provider).equals("All")) provider = null;
		String providerString = provider != null ? provider.toString() : null;
		
		progressBar.setVisibility(View.VISIBLE);
		
		ApiClient.getClient().getDefinitions(word, providerString).enqueue(new Callback<>() {
			@Override
			public void onResponse(Call<List<WordDefinition>> call, Response<List<WordDefinition>> response) {
				progressBar.setVisibility(View.GONE);
				if (response.isSuccessful() && response.body() != null) {
					saveSearch(word);
					displayResults(response.body());
				}
				else {
					resultTextView.setText("No definitions found for: " + word);
				}
			}
			
			@Override
			public void onFailure(Call<List<WordDefinition>> call, Throwable t) {
				progressBar.setVisibility(View.GONE);
				resultTextView.setText("Error: Connection failed.");
			}
		});
	}
	
	private void displayResults(List<WordDefinition> entries) {
		StringBuilder sb = new StringBuilder();
		for (WordDefinition entry : entries) {
			sb.append("Word: ").append(entry.getWord()).append("\n");
			sb.append("Definition: ").append(entry.getDefinition()).append("\n");
			if (entry.getSynonyms() != null && !entry.getSynonyms().isEmpty()) {
				sb.append("Synonyms: ").append(String.join(", ", entry.getSynonyms())).append("\n");
			}
			sb.append("\n---\n\n");
		}
		resultTextView.setText(sb.toString());
	}
	
	private void saveSearch(String word) {
		historyManager.saveSearch(word);
		autoCompleteAdapter.add(word);
	}
}
