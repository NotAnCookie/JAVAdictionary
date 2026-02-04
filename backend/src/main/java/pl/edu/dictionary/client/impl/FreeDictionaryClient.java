package pl.edu.dictionary.client.impl;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import pl.edu.dictionary.client.DictionaryClient;
import pl.edu.dictionary.client.RawDictionaryClient;
import pl.edu.dictionary.client.LanguageAwareDictionaryClient;
import pl.edu.dictionary.client.dto.Entry;
import pl.edu.dictionary.client.dto.FreeDictionaryResponse;
import pl.edu.dictionary.client.dto.Sense;
import pl.edu.dictionary.exception.WordNotFoundException;
import pl.edu.dictionary.model.Language;
import pl.edu.dictionary.model.WordDefinition;

import java.util.List;
import java.util.Set;

@Component("freeDictionaryClient")
public class FreeDictionaryClient
        implements DictionaryClient, RawDictionaryClient, LanguageAwareDictionaryClient {

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String BASE_URL =
            "https://freedictionaryapi.com/api/v1/entries/";

    @Override
    public WordDefinition getWordDefinition(String word, Language language) {

        String langCode = mapLanguage(language);
        String url = BASE_URL + langCode + "/" + word;

        FreeDictionaryResponse response =
                restTemplate.getForObject(url, FreeDictionaryResponse.class);

        if (response == null || response.entries == null || response.entries.isEmpty()) {
            throw new WordNotFoundException(word, "freeDictionaryClient");
        }

        Entry firstEntry = response.entries.get(0);

        if (firstEntry.senses == null || firstEntry.senses.isEmpty()) {
            throw new WordNotFoundException(word, "freeDictionaryClient");
        }

        Sense firstSense = firstEntry.senses.get(0);

        WordDefinition result = new WordDefinition();
        result.setWord(word);
        result.setDefinition(firstSense.definition);
        result.setSynonyms(firstSense.synonyms != null ? firstSense.synonyms : List.of());
        //result.setProvider("freeDictionaryClient"); // ustawiane w service

        return result;
    }



    @Override
    public WordDefinition getWordDefinition(String word) {
        return getWordDefinition(word, Language.EN);
    }

    // ---------------- language ----------------

    private static final Set<Language> SUPPORTED = Set.of(
            Language.EN,
            Language.DE,
            Language.FR,
            Language.ES
    );

    @Override
    public Set<Language> getSupportedLanguages() {
        return SUPPORTED;
    }

    @Override
    public String mapLanguage(Language language) {
        return language.getCode();
    }

    // ---------------- RAW ----------------

    @Override
    public Object fetchWordRaw(String word) {
        return restTemplate.getForObject(
                BASE_URL + "en/" + word,
                Object.class
        );
    }
}
