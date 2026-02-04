package pl.edu.dictionary.exception;

public class ProviderNotFoundException extends RuntimeException {
    public ProviderNotFoundException(String provider) {
        super("Unknown dictionary provider: " + provider);
    }
}
