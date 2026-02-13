package pl.edu.dictionary.models;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.Set;

public class DictionaryProvider {
	private String id;
	private boolean languageAware;
	private Set<Language> supportedLanguages;
	
	public DictionaryProvider() {}
	
	public DictionaryProvider(String id, boolean languageAware, Set<Language> supportedLanguages) {
		this.id = id;
		this.languageAware = languageAware;
		this.supportedLanguages = supportedLanguages;
	}
	
	public String getId() {
		return id;
	}
	
	public boolean isLanguageAware() {
		return languageAware;
	}
	
	public Set<Language> getSupportedLanguages() {
		return supportedLanguages;
	}
	
	@NonNull
	@Override
	public String toString() {
		var name = id;
		if (name.endsWith("Client"))
			name = name.substring(0, name.length() - "Client".length());
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		return name;
	}
	
	
	public static final DictionaryProvider ALL = new DictionaryProvider("All providers", false, Collections.emptySet());
	
}