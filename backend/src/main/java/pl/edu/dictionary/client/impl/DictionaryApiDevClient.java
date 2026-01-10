package pl.edu.dictionary.client.impl;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import pl.edu.dictionary.client.DictionaryClient;
import pl.edu.dictionary.client.dto.DictionaryApiResponse;
import pl.edu.dictionary.model.WordDefinition;

@Component("dictionaryApiDevClient")
public class DictionaryApiDevClient implements DictionaryClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String API_URL =
            "https://api.dictionaryapi.dev/api/v2/entries/en/";

    @Override
    public WordDefinition getWordDefinition(String word) {

        DictionaryApiResponse[] response =
                restTemplate.getForObject(API_URL + word, DictionaryApiResponse[].class);

        if (response == null || response.length == 0) {
            return new WordDefinition(word, "No definition found");
        }

        var firstMeaning = response[0].meanings.get(0);
        var firstDefinition = firstMeaning.definitions.get(0);

        return new WordDefinition(word, firstDefinition.definition);
    }

    // 🔧 pomocnicze – TYLKO do debugowania
    public Object fetchWordRaw(String word) {
        return restTemplate.getForObject(API_URL + word, Object.class);
    }
}
