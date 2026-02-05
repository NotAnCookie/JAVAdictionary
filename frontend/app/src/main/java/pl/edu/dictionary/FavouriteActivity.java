package pl.edu.dictionary;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class FavouriteActivity extends AppCompatActivity {
	
	private FavouriteManager favouriteManager;
	private ArrayAdapter<String> adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_favourite);
		setSupportActionBar(findViewById(R.id.toolbar));
		
		favouriteManager = new FavouriteManager(this);
		
		ListView listView = findViewById(R.id.favouriteListView);
		
		var favouriteList = favouriteManager.getFavourites();
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, favouriteList);
		listView.setAdapter(adapter);
		
		// When a user clicks an item, go back and search it
		listView.setOnItemClickListener((parent, view, position, id) -> {
			String selectedWord = favouriteList.get(position);
			Intent resultIntent = new Intent();
			resultIntent.putExtra("selected_word", selectedWord);
			setResult(RESULT_OK, resultIntent);
			finish();
		});
		
		listView.setOnItemLongClickListener((parent, view, position, id) -> {
			showDeleteConfirmationDialog(favouriteList.get(position));
			return true;
		});
	}
	
	private void showDeleteConfirmationDialog(String word) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Delete Favourite Entry");
		builder.setMessage("Are you sure you want to remove '" + word + "' from the favourites list?");
		builder.setPositiveButton("Yes", (dialog, which) -> {
			if (!favouriteManager.removeFavourite(word))
				Log.e("FavouriteActivity", "Failed to remove favourite: " + word);
			adapter.remove(word);
			adapter.notifyDataSetChanged();
		});
		builder.setNegativeButton("No", null);
		builder.show();
	}
	
}