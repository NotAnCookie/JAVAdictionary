package pl.edu.dictionary.api;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import pl.edu.dictionary.service.DictionaryRawService;

/**
 * Development-only REST controller.
 * Used during development.
 * Active only with the dev profile.
 */


@RestController
@RequestMapping("/dictionary/dev")
@Profile("dev")
public class DictionaryDevController {
    private final DictionaryRawService service;

    public DictionaryDevController(DictionaryRawService service) {
        this.service = service;
    }

    @GetMapping("/raw/{word}")
    public Object getRaw(@PathVariable String word) {
        return service.getRaw(word);
    }
}
