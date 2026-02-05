package pl.edu.dictionary;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.icu.text.BreakIterator;
import android.os.Build;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import pl.edu.dictionary.models.DictionaryProvider;
import pl.edu.dictionary.models.Language;
import pl.edu.dictionary.models.WordDefinition;
import retrofit2.HttpException;

public class DefinitionActivity extends AppCompatActivity {
	public static final String WORD_DEFINITION_EXTRA = "word_definition";
	public static final String LANGUAGE_EXTRA = "language";
	
	private final CompositeDisposable disposables = new CompositeDisposable();
	
	private ListView definitionsListView;
	
	private LinearLayout lookupLayout;
	private TextView lookupTextView;
	private ProgressBar lookupProgressBar;
	
	private WordDefinition[] wordDefinitions;
	@Nullable
	private Language language;
	
	private SearchService searchService;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_definition);
		Toolbar actionBar = findViewById(R.id.toolbar);
		setSupportActionBar(actionBar);
		
		View backgroundClickView = findViewById(R.id.backgroundClickView);
		definitionsListView = findViewById(R.id.definitionListView);
		lookupLayout = findViewById(R.id.lookupLayout);
		lookupTextView = findViewById(R.id.lookupTextView);
		lookupProgressBar = findViewById(R.id.lookupProgressBar);
		
		MainActivity.setInsets(actionBar, findViewById(R.id.mainLayout2));
		
		searchService = new SearchService(this);
		
		wordDefinitions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
				? getIntent().getParcelableArrayExtra(WORD_DEFINITION_EXTRA, WordDefinition.class)
				: (WordDefinition[]) getIntent().getParcelableArrayExtra(WORD_DEFINITION_EXTRA);
		int lang = getIntent().getIntExtra(LANGUAGE_EXTRA, -1);
		language = lang == -1 ? null : Language.values()[lang];
		
		if (wordDefinitions == null || wordDefinitions.length == 0) {
			actionBar.setTitle("Not Found");
			TextView errorTextView = findViewById(R.id.errorTextView);
			errorTextView.setText("Word not found.");
			definitionsListView.setVisibility(View.GONE);
			return;
		}
		
		displayDefinitions(wordDefinitions);
		
		backgroundClickView.setOnClickListener(v -> hideLookup());
		
		lookupTextView.setOnClickListener(v -> lookupWord(lookupTextView.getText().toString().trim()));
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
				convertView.setOnClickListener(v -> hideLookup());
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
		setSpannableText(definitionTextView, wordDefinition.getDefinition(), false);
		if (wordDefinition.getSynonyms().isEmpty())
			synonymsTextView.setVisibility(View.GONE);
		else
			setSpannableText(synonymsTextView, "Synonyms: " + TextUtils.join(", ", wordDefinition.getSynonyms()), true);
	}
	
	private void setSpannableText(TextView textView, String text, boolean skipFirstWord) {
		text = text.trim();
		textView.setMovementMethod(new StrictLinkMovementMethod(this::hideLookup));
		textView.setText(text, TextView.BufferType.SPANNABLE);
		
		Spannable spans = (Spannable) textView.getText();
		BreakIterator iterator = BreakIterator.getWordInstance(Locale.getDefault());
		iterator.setText(text);
		
		int start = iterator.first();
		boolean isFirstWord = true;
		
		for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
			String possibleWord = text.substring(start, end).trim();
			
			if (possibleWord.isEmpty())
				continue;
			
			// Skip first word if requested
			if (skipFirstWord && isFirstWord) {
				isFirstWord = false;
				continue;
			}
			isFirstWord = false;
			
			if (Character.isLetterOrDigit(possibleWord.charAt(0))) {
				spans.setSpan(
						getClickableSpan(possibleWord),
						start,
						end,
						Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
				);
			}
		}
	}
	
	private ClickableSpan getClickableSpan(String word) {
		return new ClickableSpan() {
			final String mWord = word;
			
			@Override
			public void onClick(@NonNull View widget) {
				Log.d("tapped on:", mWord);
				displayLookup(mWord);
			}
			
			public void updateDrawState(@NonNull TextPaint ds) {
				// don't underline the text
				// super.updateDrawState(ds);
			}
		};
	}
	public static class StrictLinkMovementMethod extends LinkMovementMethod {
		
		public interface OutsideClickListener {
			void onOutsideClick();
		}
		
		private final OutsideClickListener outsideClickListener;
		
		public StrictLinkMovementMethod(OutsideClickListener listener) {
			this.outsideClickListener = listener;
		}
		
		@Override
		public boolean onTouchEvent(TextView widget, Spannable buffer, MotionEvent event) {
			int action = event.getAction();
			
			if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
				int x = (int) event.getX();
				int y = (int) event.getY();
				
				x -= widget.getTotalPaddingLeft();
				y -= widget.getTotalPaddingTop();
				
				x += widget.getScrollX();
				y += widget.getScrollY();
				
				Layout layout = widget.getLayout();
				int line = layout.getLineForVertical(y);
				
				// Prevent clicks past the last character
				if (x > layout.getLineWidth(line)) {
					if (action == MotionEvent.ACTION_UP)
						outsideClickListener.onOutsideClick();
					return true;
				}
				
				int off = layout.getOffsetForHorizontal(line, x);
				ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
				
				if (link.length != 0) {
					if (action == MotionEvent.ACTION_UP)
						link[0].onClick(widget);
					return true;
				} else {
					// Clicked inside text but not on a span
					if (action == MotionEvent.ACTION_UP)
						outsideClickListener.onOutsideClick();
					return true;
				}
			}
			
			return super.onTouchEvent(widget, buffer, event);
		}
	}
	
	
	private void displayLookup(@NonNull String lookupText) {
		lookupTextView.setText(lookupText);
		lookupLayout.setVisibility(View.VISIBLE);
		// set color to ?attr/colorOnSecondaryContainer
		TypedValue typedValue = new TypedValue();
		if (getTheme().resolveAttribute(com.google.android.material.R.attr.colorOnSecondaryContainer, typedValue, true))
			lookupTextView.setTextColor(typedValue.data);
		else {
			Log.e("DefinitionActivity", "textColorSecondary not found");
			lookupTextView.setTextColor(Color.BLACK);
		}
	}
	
	private void hideLookup() {
		lookupLayout.setVisibility(View.GONE);
	}
	
	private void lookupError(String message) {
		lookupTextView.setText(message);
		lookupLayout.setVisibility(View.VISIBLE);
		// set color to ?attr/colorError
		TypedValue typedValue = new TypedValue();
		if (getTheme().resolveAttribute(android.R.attr.colorError, typedValue, true))
			lookupTextView.setTextColor(typedValue.data);
		else
			Log.e("DefinitionActivity", "colorError not found");
	
	}
	
	private void lookupWord(String word) {
		var providers = Arrays.stream(wordDefinitions)
				.map(WordDefinition::getProvider)
				.map(str -> new DictionaryProvider(str, false, Collections.emptySet()))
				.collect(Collectors.toList());
		Consumer<Disposable> doOnSubscribe = d -> lookupProgressBar.setVisibility(View.VISIBLE);
		Action doFinally = () -> lookupProgressBar.setVisibility(View.INVISIBLE);
		Consumer<List<WordDefinition>> onSuccess = definitions -> launchActivity(this, definitions, language);
		Consumer<Throwable> onError = t -> {
			if (t instanceof SearchService.WordNotFoundException wordNotFoundException) {
				lookupError(wordNotFoundException.getMessage());
				return;
			}
			Log.e("DefinitionActivity", "Lookup error", t);
			if (t instanceof HttpException httpException) {
				if (httpException.code() >= 500) {
					lookupError("Server error");
				}
				else {
					lookupError("Unknown error");
				}
			}
			else {
				lookupError("Connection error");
			}
		};
		Disposable disposable = providers.size() == 1
				? searchService.performSearch(word, providers.get(0), language, doOnSubscribe, doFinally, onSuccess, onError)
				: searchService.performAllSearch(word, providers, doOnSubscribe, doFinally, onSuccess, onError);
		
		if (disposable != null)
			disposables.add(disposable);
	}
	
	public static void launchActivity(Activity context, List<WordDefinition> wordDefinitions, @Nullable Language language) {
		Intent intent = new Intent(context, DefinitionActivity.class);
		intent.putExtra(DefinitionActivity.WORD_DEFINITION_EXTRA, wordDefinitions.toArray(new WordDefinition[0]));
		intent.putExtra(DefinitionActivity.LANGUAGE_EXTRA, language == null ? -1 : language.ordinal());
		context.startActivity(intent);
	}
	
	@Override
	public boolean onOptionsItemSelected(@NonNull MenuItem item) {
		int itemId = item.getItemId();
		if (itemId == R.id.action_search) {
			// Handle search action
			Intent intent = new Intent(this, MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			startActivity(intent);
			return true;
		} else if (itemId == R.id.action_favourite_add) {
			// Handle favourite action
			searchService.saveFavourite(wordDefinitions[0].getWord());
			toggleFavouriteIcon(true);
			return true;
		} else if (itemId == R.id.action_favourite_remove) {
			// Handle favourite action
			searchService.removeFavourite(wordDefinitions[0].getWord());
			toggleFavouriteIcon(false);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	private void toggleFavouriteIcon(boolean isFavourite) {
		MenuItem add = actionBar.getMenu().findItem(R.id.action_favourite_add);
		MenuItem remove = actionBar.getMenu().findItem(R.id.action_favourite_remove);
		
		if (add != null) add.setVisible(!isFavourite);
		if (remove != null) remove.setVisible(isFavourite);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.definition_menu, menu);
		if (wordDefinitions != null && wordDefinitions.length > 0)
			toggleFavouriteIcon(searchService.isFavourite(wordDefinitions[0].getWord()));
		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		invalidateOptionsMenu();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		disposables.clear();
	}
}