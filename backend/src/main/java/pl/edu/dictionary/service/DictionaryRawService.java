package pl.edu.dictionary.service;
//
// Service for TESTs
//

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import pl.edu.dictionary.client.RawDictionaryClient;

/**
 * Development-only dictionary Service.
 * Used for retrieving raw API responses.
 * Active only with the dev profile.
 */


@Service
@Profile("dev")
public class DictionaryRawService {
    private final RawDictionaryClient rawClient;

    public DictionaryRawService(RawDictionaryClient rawClient) {
        this.rawClient = rawClient;
    }

    public Object getRaw(String word) {
        return rawClient.fetchWordRaw(word);
    }
}
