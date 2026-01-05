package pl.edu.dictionary.client;

import org.springframework.stereotype.Component;
import pl.edu.dictionary.model.WordDefinition;

@Component
public class DictionaryApiClient {

    public WordDefinition fetchWord(String word) {
        // todo
        WordDefinition wd = new WordDefinition();
        wd.setWord(word);
        wd.setDefinition("To jest przykładowa definicja.");
        return wd;
    }
}
