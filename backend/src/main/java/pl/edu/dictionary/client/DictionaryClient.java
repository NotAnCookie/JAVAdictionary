package pl.edu.dictionary.client;

import pl.edu.dictionary.model.WordDefinition;

public interface DictionaryClient {
    WordDefinition getWordDefinition(String word);
}
