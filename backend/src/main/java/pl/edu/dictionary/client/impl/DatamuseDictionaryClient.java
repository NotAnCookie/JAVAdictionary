package pl.edu.dictionary.client.impl;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import pl.edu.dictionary.client.DictionaryClient;
import pl.edu.dictionary.client.RawDictionaryClient;
import pl.edu.dictionary.client.dto.DatamuseResponse;
import pl.edu.dictionary.model.WordDefinition;

import java.util.Arrays;
import java.util.List;

/**
 * Dictionary client implementation for Datamuse.
 *
 * Fetches and maps word synonyms.
 * DO NOT have definitions for words
 * (sending placeholder in its place)
 */

@Component("datamuseClient")
public class DatamuseDictionaryClient implements DictionaryClient, RawDictionaryClient {
    private final RestTemplate restTemplate = new RestTemplate();

    private static final String API_URL =
            "https://api.datamuse.com/words?ml=";

    @Override
    public WordDefinition getWordDefinition(String word) {

        DatamuseResponse[] response =
                restTemplate.getForObject(API_URL + word, DatamuseResponse[].class);

        List<String> synonyms = Arrays.stream(response)
                .map(r -> r.word)
                .limit(10)
                .toList();

        WordDefinition def = new WordDefinition();
        def.setWord(word);
        def.setDefinition("Definition not available for this provider.");
        def.setSynonyms(synonyms);

        return def;
    }

    // TESTs only
    public Object fetchWordRaw(String word) {
        return restTemplate.getForObject(API_URL + word, Object.class);
    }


}
