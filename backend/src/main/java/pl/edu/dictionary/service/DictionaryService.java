package pl.edu.dictionary.service;

import org.springframework.stereotype.Service;
import pl.edu.dictionary.model.WordDefinition;
import pl.edu.dictionary.client.DictionaryApiClient;

@Service
public class DictionaryService {

    private final DictionaryApiClient client;

    public DictionaryService(DictionaryApiClient client) {
        this.client = client;
    }

    public WordDefinition getWord(String word) {
        // tutaj można dodać walidację, cache, mapping JSON -> model
        return client.fetchWord(word);
    }
}
