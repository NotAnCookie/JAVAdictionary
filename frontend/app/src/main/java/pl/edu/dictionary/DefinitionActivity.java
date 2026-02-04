package pl.edu.dictionary;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Objects;

import pl.edu.dictionary.models.WordDefinition;

public class DefinitionActivity extends AppCompatActivity {
	public static final String WORD_DEFINITION_EXTRA = "word_definition";
	
	private ListView definitionsListView;
	
	private WordDefinition[] wordDefinitions;
	private Toolbar actionBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_definition);
		actionBar = findViewById(R.id.toolbar);
		setSupportActionBar(actionBar);
		
		definitionsListView = findViewById(R.id.definitionListView);
		
		wordDefinitions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
				? getIntent().getParcelableArrayExtra(WORD_DEFINITION_EXTRA, WordDefinition.class)
				: (WordDefinition[]) getIntent().getParcelableArrayExtra(WORD_DEFINITION_EXTRA);
		
		if (wordDefinitions == null || wordDefinitions.length == 0) {
			actionBar.setTitle("Not Found");
			TextView errorTextView = findViewById(R.id.errorTextView);
			errorTextView.setText("Word not found.");
			definitionsListView.setVisibility(View.GONE);
			return;
		}
		
		displayDefinitions(wordDefinitions);
		
	}
	
	
	private void displayDefinitions(@NonNull WordDefinition[] wordDefinitions) {
		Objects.requireNonNull(getSupportActionBar()).setTitle(wordDefinitions[0].getWord());
		var arrayAdapter = new ArrayAdapter<>(this, R.layout.word_definition, wordDefinitions) {
			@NonNull
			@Override
			public View getView(int position, View convertView, @NonNull ViewGroup parent) {
				if (convertView == null) {
					convertView = getLayoutInflater().inflate(R.layout.word_definition, parent, false);
				}
				displayDefinition(convertView, wordDefinitions[position]);
				return convertView;
			}
		};
		definitionsListView.setAdapter(arrayAdapter);
		
	}
	private void displayDefinition(View view, @NonNull WordDefinition wordDefinition) {
		TextView providerTextView = view.findViewById(R.id.providerTextView);
		TextView definitionTextView = view.findViewById(R.id.definitionTextView);
		TextView synonymsTextView = view.findViewById(R.id.synonymsTextView);
		
		providerTextView.setText(wordDefinition.getProvider());
		definitionTextView.setText(wordDefinition.getDefinition());
		if (wordDefinition.getSynonyms().isEmpty())
			synonymsTextView.setVisibility(View.GONE);
		else
			synonymsTextView.setText("Synonyms: " + TextUtils.join(", ", wordDefinition.getSynonyms()));
	}
	
	
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_search) {
			// Handle search action
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.definition_menu, menu);
		return true;
	}
	
}