package pl.edu.dictionary.exception;

public class LanguageNotSupportedException extends RuntimeException {
    public LanguageNotSupportedException(String language) {
        super("Language not supported: " + language);
    }
}
