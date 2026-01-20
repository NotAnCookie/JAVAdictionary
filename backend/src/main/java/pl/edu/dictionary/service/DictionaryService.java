package pl.edu.dictionary.service;

import org.springframework.stereotype.Service;
import pl.edu.dictionary.client.DictionaryClient;
import pl.edu.dictionary.client.LanguageAwareDictionaryClient;
import pl.edu.dictionary.client.dto.DictionaryProviderDto;
import pl.edu.dictionary.model.DictionaryProvider;
import pl.edu.dictionary.model.Language;
import pl.edu.dictionary.model.WordDefinition;
import pl.edu.dictionary.client.DictionaryClientFactory;
import pl.edu.dictionary.client.dto.DictionaryApiResponse;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        DictionaryProvider provider = factory.getDefaultProvider();

        WordDefinition result =
                factory.getDefaultClient().getWordDefinition(word);

        result.setProvider(provider);
        return result;
    }

    public WordDefinition getWord(String word, String provider) {
        DictionaryProvider resolved = factory.resolveProvider(provider);

        WordDefinition result =
                factory.getClient(provider).getWordDefinition(word);

        result.setProvider(resolved);
        return result;
    }

    public WordDefinition getWord(String word, Language lang) {
        return getWord(word, null, lang);
    }

    public WordDefinition getWord(String word, String provider, Language language) {

        DictionaryProvider resolvedProvider =
                (provider == null)
                        ? factory.getDefaultProvider()
                        : factory.resolveProvider(provider);

        DictionaryClient client =
                (provider == null)
                        ? factory.getDefaultClient()
                        : factory.getClient(provider);

        WordDefinition result;

        if (client instanceof LanguageAwareDictionaryClient langClient) {

            if (!langClient.getSupportedLanguages().contains(language)) {
                throw new IllegalArgumentException("Language not supported");
            }

            result = client.getWordDefinition(word, language);
        } else {
            result = client.getWordDefinition(word);
        }

        result.setProvider(resolvedProvider);
        return result;
    }


    public List<DictionaryProviderDto> getProviders() {
        return factory.getAllClients().entrySet().stream()
                .map(entry -> {
                    String id = entry.getKey();
                    var client = entry.getValue();

                    if (client instanceof LanguageAwareDictionaryClient langClient) {
                        return new DictionaryProviderDto(
                                id,
                                true,
                                langClient.getSupportedLanguages()
                        );
                    }

                    return new DictionaryProviderDto(
                            id,
                            false,
                            Set.of()
                    );
                })
                .collect(Collectors.toList());
    }


}
