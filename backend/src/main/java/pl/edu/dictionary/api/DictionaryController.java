package pl.edu.dictionary.api;

import org.springframework.web.bind.annotation.*;

import pl.edu.dictionary.model.WordDefinition;
import pl.edu.dictionary.service.DictionaryService;

@RestController
@RequestMapping("/dictionary")
public class DictionaryController {

    private final DictionaryService service;

    public DictionaryController(DictionaryService service) {
        this.service = service;
    }

    //
    @GetMapping("/{word}")
    public WordDefinition getWord(
            @PathVariable String word,
            @RequestParam(required = false) String provider
    ) {
        return provider == null
                ? service.getWord(word)
                : service.getWord(word, provider);
    }
}
