package pl.edu.dictionary.client;

import java.util.List;

public interface AutocompleteClient {
    List<String> suggest(String query);
    List<String> spellcheck(String query);

    // added limits --- for cleaner results
    List<String> suggest(String query, int limit);
    List<String> spellcheck(String query, int limit);
}
