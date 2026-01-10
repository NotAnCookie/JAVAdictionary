package pl.edu.dictionary.service;

import org.springframework.stereotype.Service;
import pl.edu.dictionary.model.WordDefinition;
import pl.edu.dictionary.client.DictionaryApiClient;
import pl.edu.dictionary.client.dto.DictionaryApiResponse;

@Service
public class DictionaryService {

    private final DictionaryApiClient client;

    public DictionaryService(DictionaryApiClient client) {
        this.client = client;
    }

    public WordDefinition getWord(String word) {
        DictionaryApiResponse[] response = client.fetchWord(word);

        if (response == null || response.length == 0) {
            return new WordDefinition(word, "No definition found");
        }

        var firstMeaning = response[0].meanings.get(0);
        var firstDefinition = firstMeaning.definitions.get(0);

        return new WordDefinition(
                word,
                firstDefinition.definition
        );
    }

    // do testów
    public Object getWordRaw(String word) {
        return client.fetchWordRaw(word);
    }
}
