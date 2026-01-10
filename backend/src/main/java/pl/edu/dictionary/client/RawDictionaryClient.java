package pl.edu.dictionary.client;

/**
 * Interface for testing raw responses from API clients.
 */

public interface RawDictionaryClient {
    Object fetchWordRaw(String word);
}
