package pl.edu.dictionary.service;

import org.springframework.stereotype.Service;
import pl.edu.dictionary.client.DictionaryClient;
import pl.edu.dictionary.client.LanguageAwareDictionaryClient;
import pl.edu.dictionary.model.Language;
import pl.edu.dictionary.model.WordDefinition;
import pl.edu.dictionary.client.DictionaryClientFactory;
import pl.edu.dictionary.client.dto.DictionaryApiResponse;

/**
 * Main dictionary service.
 * Selects appropriate dictionary provider.
 * data returned from external APIs.
 */


@Service
public class DictionaryService {

    private final DictionaryClientFactory factory;

    public DictionaryService(DictionaryClientFactory factory) {
        this.factory = factory;
    }

    public WordDefinition getWord(String word) {
        return factory.getDefaultClient().getWordDefinition(word);
    }

    public WordDefinition getWord(String word, String provider) {
        return factory.getClient(provider).getWordDefinition(word);
    }

    public WordDefinition getWord(String word, Language lang) {
        return getWord(word, null, lang);
    }

    public WordDefinition getWord(String word, String provider, Language language) {
        DictionaryClient client = (provider == null)
                ? factory.getDefaultClient()
                : factory.getClient(provider);

        if (client instanceof LanguageAwareDictionaryClient langClient) {

            if (!langClient.getSupportedLanguages().contains(language)) {
                throw new IllegalArgumentException("Language not supported");
            }

            return client.getWordDefinition(word, language);
        }

        // if provider isn't language aware → fallback EN
        return client.getWordDefinition(word);
    }


}
