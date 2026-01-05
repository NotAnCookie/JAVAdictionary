package pl.edu.dictionary.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pl.edu.dictionary.model.WordDefinition;
import pl.edu.dictionary.service.DictionaryService;

@RestController
@RequestMapping("/dictionary")
public class DictionaryController {

    private final DictionaryService service;

    public DictionaryController(DictionaryService service) {
        this.service = service;
    }

    @GetMapping("/{word}")
    public WordDefinition getWord(@PathVariable String word) {
        return service.getWord(word);
    }
}
