package pl.edu.dictionary.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import pl.edu.dictionary.service.DictionaryRawService;

/**
 * Development-only REST controller.
 * Used during development.
 * Active only with the dev profile.
 */

@Tag(name = "DictionaryDev", description = "Dictionary dev lookup endpoints")
@RestController
@RequestMapping("/dictionary/dev")
@Profile("dev")
public class DictionaryDevController {
    private final DictionaryRawService service;

    public DictionaryDevController(DictionaryRawService service) {
        this.service = service;
    }

    @Operation(
            summary = "Get raw response for word",
            description = "Returns raw JSON for a given word"
    )
    @GetMapping("/raw/{word}")
    public Object getRaw(
            @Parameter(description = "Word to search for", example = "like")
            @PathVariable String word,

            @Parameter(
                    description = "Optional dictionary provider",
                    example = "dictionaryApiDevClient"
            )
            @RequestParam(required = false) String provider
    ) {
        return provider == null
                ? service.getRawDefault(word)
                : service.getRaw(word, provider);
    }
}
