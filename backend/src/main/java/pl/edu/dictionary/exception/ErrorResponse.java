package pl.edu.dictionary.exception;

public record ErrorResponse(
        String code,
        String message
) {}

