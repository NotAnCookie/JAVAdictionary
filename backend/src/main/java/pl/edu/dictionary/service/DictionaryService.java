package pl.edu.dictionary.service;

import org.springframework.stereotype.Service;
import pl.edu.dictionary.model.WordDefinition;
import pl.edu.dictionary.client.DictionaryClientFactory;
import pl.edu.dictionary.client.dto.DictionaryApiResponse;

/**
 * Main dictionary service.
 * Selects appropriate dictionary provider.
 * data returned from external APIs.
 */


@Service
public class DictionaryService {

    private final DictionaryClientFactory factory;

    public DictionaryService(DictionaryClientFactory factory) {
        this.factory = factory;
    }

    public WordDefinition getWord(String word) {
        return factory.getDefaultClient().getWordDefinition(word);
    }

    public WordDefinition getWord(String word, String provider) {
        return factory.getClient(provider).getWordDefinition(word);
    }

}
