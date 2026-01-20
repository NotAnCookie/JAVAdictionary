package pl.edu.dictionary.model;

import java.util.Dictionary;
import java.util.List;

public class WordDefinition {
    private String word;
    private String definition;
    private List<String> synonyms;
    private DictionaryProvider provider;

    // constructors
    public WordDefinition() {}
    public WordDefinition(String word, String definition) {
        this.word = word;
        this.definition = definition;
    }

    // getters and setters
    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }

    public DictionaryProvider getProvider() { return provider; }

    public void setProvider(DictionaryProvider provider) { this.provider = provider; }
}
