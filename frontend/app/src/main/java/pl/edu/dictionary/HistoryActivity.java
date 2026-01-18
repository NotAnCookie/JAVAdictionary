package pl.edu.dictionary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
	
	private SearchHistoryManager historyManager;
	private ArrayAdapter<String> adapter;
	private List<String> historyList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		
		historyManager = new SearchHistoryManager(this);
		ListView listView = findViewById(R.id.historyListView);
		Button clearButton = findViewById(R.id.clearHistoryButton);
		
		historyList = historyManager.getHistory();
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, historyList);
		listView.setAdapter(adapter);
		
		// When a user clicks a history item, go back and search it
		listView.setOnItemClickListener((parent, view, position, id) -> {
			String selectedWord = historyList.get(position);
			Intent resultIntent = new Intent();
			resultIntent.putExtra("selected_word", selectedWord);
			setResult(RESULT_OK, resultIntent);
			finish();
		});
		listView.setOnItemLongClickListener((parent, view, position, id) -> {
			showDeleteConfirmationDialog(historyList.get(position));
			return true;
		});
		
		clearButton.setOnClickListener(v -> {
			showClearConfirmationDialog();
		});
	}
	
	private void showDeleteConfirmationDialog(String word) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Delete History Entry");
		builder.setMessage("Are you sure you want to remove '" + word + "' from the history?");
		builder.setPositiveButton("Yes", (dialog, which) -> {
			if (!historyManager.removeSearch(word))
				Log.e("HistoryActivity", "Failed to remove search: " + word);
			adapter.remove(word);
			adapter.notifyDataSetChanged();
		});
		builder.setNegativeButton("No", null);
		builder.show();
	}
	
	private void showClearConfirmationDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Clear History");
		builder.setMessage("Are you sure you want to clear the history?");
		builder.setPositiveButton("Yes", (dialog, which) -> {
			historyManager.clearHistory();
			adapter.clear();
			adapter.notifyDataSetChanged();
		});
		builder.setNegativeButton("No", null);
		builder.show();
	}
}