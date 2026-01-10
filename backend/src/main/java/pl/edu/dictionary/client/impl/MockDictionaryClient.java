package pl.edu.dictionary.client.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import pl.edu.dictionary.client.DictionaryClient;
import pl.edu.dictionary.model.WordDefinition;

/**
 * Mock dictionary client.
 * Returns data without calling external APIs.
 */

@Component("mockDictionaryClient")
@Profile({"dev","test"})
public class MockDictionaryClient implements DictionaryClient{
    @Override
    public WordDefinition getWordDefinition(String word) {
        return new WordDefinition(
                word,
                "Mock definition for word: " + word
        );
    }
}
