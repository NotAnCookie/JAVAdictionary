package pl.edu.dictionary.client.impl;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import pl.edu.dictionary.client.DictionaryClient;
import pl.edu.dictionary.client.RawDictionaryClient;
import pl.edu.dictionary.client.dto.DictionaryApiResponse;
import pl.edu.dictionary.model.WordDefinition;

import java.util.List;

/**
 * Dictionary client implementation for DictionaryAPI.dev.
 *
 * Fetches and maps word definitions from the public
 * DictionaryAPI.dev service.
 */


@Component("dictionaryApiDevClient")
public class DictionaryApiDevClient implements DictionaryClient, RawDictionaryClient {

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

        WordDefinition result = new WordDefinition();
        result.setWord(word);
        result.setDefinition(firstDefinition.definition);
        result.setSynonyms(
                firstDefinition.synonyms != null
                        ? firstDefinition.synonyms
                        : List.of()
        );

        //return new WordDefinition(word, firstDefinition.definition);
        return result;
    }

    // TESTs only
    public Object fetchWordRaw(String word) {
        return restTemplate.getForObject(API_URL + word, Object.class);
    }
}
