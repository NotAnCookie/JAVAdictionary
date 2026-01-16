package pl.edu.dictionary.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.edu.dictionary.client.DictionaryClient;
import pl.edu.dictionary.client.DictionaryClientFactory;
import pl.edu.dictionary.model.WordDefinition;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DictionaryServiceTest {
    @Test
    void shouldReturnWordDefinitionFromDefaultClient() {
        // arrange
        DictionaryClient mockClient = Mockito.mock(DictionaryClient.class);

        WordDefinition expected = new WordDefinition();
        expected.setWord("test");
        expected.setDefinition("Test definition");

        Mockito.when(mockClient.getWordDefinition("test"))
                .thenReturn(expected);

        DictionaryClientFactory factory =
                new DictionaryClientFactory(
                        Map.of("dictionaryApiDevClient", mockClient)
                );

        DictionaryService service = new DictionaryService(factory);

        // act
        WordDefinition result = service.getWord("test");

        // assert
        assertEquals("test", result.getWord());
        assertEquals("Test definition", result.getDefinition());
    }
}
