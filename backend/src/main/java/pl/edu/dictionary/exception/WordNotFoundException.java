package pl.edu.dictionary.exception;

public class WordNotFoundException extends RuntimeException {
    public WordNotFoundException(String word, String provider) {
        super("Word '" + word + "' not found in provider '" + provider + "'");
    }
}
