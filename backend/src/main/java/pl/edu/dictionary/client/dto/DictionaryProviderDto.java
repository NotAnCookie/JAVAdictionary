package pl.edu.dictionary.client.dto;

import pl.edu.dictionary.model.Language;

import java.util.Set;

public class DictionaryProviderDto {
    final private String id;
    final private boolean languageAware;
    final private Set<Language> supportedLanguages;

    public DictionaryProviderDto(
            String id,
            boolean languageAware,
            Set<Language> supportedLanguages
    ) {
        this.id = id;
        this.languageAware = languageAware;
        this.supportedLanguages = supportedLanguages;
    }

    public String getId() {
        return id;
    }

    public boolean isLanguageAware() {
        return languageAware;
    }

    public Set<Language> getSupportedLanguages() {
        return supportedLanguages;
    }
}
