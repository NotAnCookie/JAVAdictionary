package pl.edu.dictionary.models;

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
	
	public static final DictionaryProvider ALL = new DictionaryProvider("All", false, Collections.emptySet());
	
}