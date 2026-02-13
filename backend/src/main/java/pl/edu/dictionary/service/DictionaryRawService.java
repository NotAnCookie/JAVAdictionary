package pl.edu.dictionary.service;
//
// Service for TESTs
//

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import pl.edu.dictionary.client.RawDictionaryClient;

import java.util.Map;

/**
 * Development-only dictionary Service.
 * Used for retrieving raw API responses.
 * Active only with the dev profile.
 */


@Service
@Profile("dev")
public class DictionaryRawService {
    private final Map<String, RawDictionaryClient> rawClients;

    public DictionaryRawService(Map<String, RawDictionaryClient> rawClients) {
        this.rawClients = rawClients;
    }

    public Object getRaw(String word, String provider) {
        RawDictionaryClient client = rawClients.get(provider);

        if (client == null) {
            throw new IllegalArgumentException("Unknown raw dictionary provider: " + provider);
        }

        return client.fetchWordRaw(word);
    }

    public Object getRawDefault(String word) {
        return rawClients.get("dictionaryApiDevClient").fetchWordRaw(word);
    }
}
