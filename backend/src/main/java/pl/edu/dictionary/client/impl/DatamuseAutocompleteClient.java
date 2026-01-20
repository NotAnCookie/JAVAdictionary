package pl.edu.dictionary.client.impl;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import pl.edu.dictionary.client.AutocompleteClient;
import pl.edu.dictionary.client.dto.DatamuseResponse;

import java.util.Arrays;
import java.util.List;

@Component("datamuseAutocompleteClient")
public class DatamuseAutocompleteClient implements AutocompleteClient {
    private final RestTemplate restTemplate = new RestTemplate();

    private static final int DEFAULT_LIMIT = 10;

    @Override
    public List<String> suggest(String query) {
        return suggest(query, DEFAULT_LIMIT);
    }

    @Override
    public List<String> spellcheck(String query) {
        return spellcheck(query, DEFAULT_LIMIT);
    }

    @Override
    public List<String> suggest(String query, int limit) {
        String url = "https://api.datamuse.com/words?sp=" + query + "*";
        return fetchWords(url, limit);
    }

    @Override
    public List<String> spellcheck(String query, int limit) {
        String url = "https://api.datamuse.com/words?sl=" + query;
        return fetchWords(url, limit);
    }

    private List<String> fetchWords(String url, int limit) {
        DatamuseResponse[] response =
                restTemplate.getForObject(url, DatamuseResponse[].class);

        if (response == null) return List.of();

        return Arrays.stream(response)
                .map(r -> r.word)
                .limit(limit)
                .toList();
    }
}
