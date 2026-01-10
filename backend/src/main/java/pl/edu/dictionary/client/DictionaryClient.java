package pl.edu.dictionary.client;

import pl.edu.dictionary.model.WordDefinition;

/**
 * Interface for external dictionary API clients.
 */

public interface DictionaryClient {
    WordDefinition getWordDefinition(String word);
}
