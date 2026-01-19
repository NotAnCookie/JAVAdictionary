package pl.edu.dictionary.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.util.List;

public class WordDefinition implements Parcelable {
	private String word;
	private String definition;
	private List<String> synonyms;
	
	public String getWord() { return word; }
	public String getDefinition() { return definition; }
	public List<String> getSynonyms() { return synonyms; }
	
	public WordDefinition() {}
	
	
	// Parcelable implementation
	
	protected WordDefinition(Parcel in) {
		word = in.readString();
		definition = in.readString();
		synonyms = in.createStringArrayList();
	}
	
	public static final Creator<WordDefinition> CREATOR = new Creator<>() {
		@Override
		public WordDefinition createFromParcel(Parcel in) {
			return new WordDefinition(in);
		}
		
		@Override
		public WordDefinition[] newArray(int size) {
			return new WordDefinition[size];
		}
	};
	
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	@Override
	public void writeToParcel(@NonNull Parcel dest, int flags) {
		dest.writeString(word);
		dest.writeString(definition);
		dest.writeStringList(synonyms);
	}
	
}

