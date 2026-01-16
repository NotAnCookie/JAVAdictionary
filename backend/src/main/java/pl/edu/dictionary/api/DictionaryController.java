package pl.edu.dictionary.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import pl.edu.dictionary.model.DictionaryProvider;
import pl.edu.dictionary.model.WordDefinition;
import pl.edu.dictionary.model.Language;
import pl.edu.dictionary.service.DictionaryService;

/**
 * Main REST controller.
 * Returns processed word definitions.
 */

@Tag(name = "Dictionary", description = "Dictionary lookup endpoints")
@RestController
@RequestMapping("/dictionary")
public class DictionaryController {

    private final DictionaryService service;

    public DictionaryController(DictionaryService service) {
        this.service = service;
    }

    @Operation(
            summary = "Get word definition",
            description = "Returns definition and synonyms for a given word"
    )
    @GetMapping("/{word}")
    public WordDefinition getWord(
            @Parameter(description = "Word to search for", example = "like")
            @PathVariable String word,

            @Parameter(
                    description = "Optional dictionary provider",
                    example = "dictionaryApiDevClient"
            )
            @RequestParam(required = false) DictionaryProvider provider,

            @Parameter(
                    description = "Optional language (default: EN)",
                    example = "EN"
            )
            @RequestParam(defaultValue = "EN") Language lang
    ) {
        return provider == null
                ? service.getWord(word, lang)
                : service.getWord(word, provider.getBeanName(), lang);
    }
}
