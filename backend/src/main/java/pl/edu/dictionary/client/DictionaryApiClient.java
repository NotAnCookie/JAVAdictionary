package pl.edu.dictionary.client;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import pl.edu.dictionary.model.WordDefinition;
import pl.edu.dictionary.client.dto.DictionaryApiResponse;

@Component
public class DictionaryApiClient {

    private final RestTemplate restTemplate = new RestTemplate();
    // adres dla API słownika DictionaryAPI.dev
    private final String API_URL = "https://api.dictionaryapi.dev/api/v2/entries/en/";


    // produkcyjna metoda (mapowana)
    public DictionaryApiResponse[] fetchWord(String word) {
        return restTemplate.getForObject(
                API_URL + word,
                DictionaryApiResponse[].class
        );
    }

    // testowa metoda – surowy JSON
    public Object fetchWordRaw(String word) {
        return restTemplate.getForObject(API_URL + word, Object.class);
    }
}
