package pl.edu.dictionary.models;

import java.util.List;

public class WordDefinition {
	private String word;
	private String definition;
	private List<String> synonyms;
	
	public String getWord() { return word; }
	public String getDefinition() { return definition; }
	public List<String> getSynonyms() { return synonyms; }
}

