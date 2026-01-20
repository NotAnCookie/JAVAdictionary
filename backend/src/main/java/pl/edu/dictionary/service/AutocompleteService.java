package pl.edu.dictionary.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import pl.edu.dictionary.client.AutocompleteClient;

import java.util.List;

@Service
public class AutocompleteService {
    private final AutocompleteClient client;

    private static final int SPELLCHECK_LIMIT = 1;

    public AutocompleteService(
            @Qualifier("datamuseAutocompleteClient") AutocompleteClient client
    ) {
        this.client = client;
    }

    public List<String> suggest(String query) {
        // normal autocomplete
        List<String> results = client.suggest(query);

        if (!results.isEmpty()) {
            return results;
        }

        // fallback -> spellcheck (with limit)
        return client.spellcheck(query, SPELLCHECK_LIMIT);
    }
}