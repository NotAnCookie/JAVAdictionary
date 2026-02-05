package pl.edu.dictionary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {
	
	private SearchHistoryManager historyManager;
	private ArrayAdapter<String> adapter;
	private List<String> historyList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		
		historyManager = new SearchHistoryManager(this);
		ListView listView = findViewById(R.id.historyListView);
		
		MainActivity.setInsets(toolbar, listView);
		
		historyList = new ArrayList<>(historyManager.getHistory());
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
	
	
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_clear) {
			showClearConfirmationDialog();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.history_menu, menu);
		return true;
	}
}