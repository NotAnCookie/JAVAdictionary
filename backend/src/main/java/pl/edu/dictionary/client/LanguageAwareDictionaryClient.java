package pl.edu.dictionary.client;

import pl.edu.dictionary.model.Language;

import java.util.Set;

public interface LanguageAwareDictionaryClient {
    /**
     * Returns supported languages for dictionary provider.
     */
    Set<Language> getSupportedLanguages();

    /**
     * Maps application language to provider-specific language code.
     */
    String mapLanguage(Language language);
}
