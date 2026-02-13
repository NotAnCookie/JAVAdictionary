package pl.edu.dictionary.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import pl.edu.dictionary.model.Language;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(WordNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWordNotFound(
            WordNotFoundException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(
                        "WORD_NOT_FOUND",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(ProviderNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleProviderNotFound(
            ProviderNotFoundException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "PROVIDER_NOT_FOUND",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(LanguageNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleLanguageNotSupported(
            LanguageNotSupportedException ex
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "INVALID_LANGUAGE",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleEnumMismatch(
            MethodArgumentTypeMismatchException ex
    ) {
        if (ex.getRequiredType() == Language.class) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse(
                            "INVALID_LANGUAGE",
                            "Unsupported language: " + ex.getValue()
                    ));
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(
                        "INVALID_ARGUMENT",
                        "Invalid request parameter"
                ));
    }


    // fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(
                        "INTERNAL_ERROR",
                        "Unexpected server error"
                ));
    }
}


