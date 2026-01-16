package pl.edu.dictionary.client.impl;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import pl.edu.dictionary.client.DictionaryClient;
import pl.edu.dictionary.client.LanguageAwareDictionaryClient;
import pl.edu.dictionary.client.RawDictionaryClient;
import pl.edu.dictionary.model.WordDefinition;
import pl.edu.dictionary.model.Language;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Mock dictionary client.
 * Returns data without calling external APIs.
 * Supports languages
 */

@Component("mockDictionaryClient")
@Profile({"dev","test"})
public class MockDictionaryClient implements DictionaryClient, RawDictionaryClient, LanguageAwareDictionaryClient {
    private static final Set<Language> SUPPORTED_LANGUAGES = Set.of(
            Language.EN,
            Language.ES,
            Language.FR
    );

    @Override
    public WordDefinition getWordDefinition(String word) {
        return buildDefinition(word, Language.EN);
    }

    @Override
    public WordDefinition getWordDefinition(String word, Language language) {
        return buildDefinition(word, language);
    }

    private WordDefinition buildDefinition(String word, Language language) {
        WordDefinition def = new WordDefinition();
        def.setWord(word);
        def.setDefinition("Mock definition for '" + word + "' [" + language + "]");
        def.setSynonyms(List.of(
                word + "_syn_1",
                word + "_syn_2",
                word + "_syn_3"
        ));
        return def;
    }

    // ---------------- language ----------------

    @Override
    public Set<Language> getSupportedLanguages() {
        return SUPPORTED_LANGUAGES;
    }

    @Override
    public String mapLanguage(Language language) {
        return language.name().toLowerCase();
    }

    // ---------------- RAW ----------------

    @Override
    public Object fetchWordRaw(String word) {
        return Map.of(
                "word", word,
                "definition", "Mock raw definition",
                "synonyms", List.of("mock1", "mock2"),
                "provider", "mockDictionaryClient"
        );
    }
}
