package pl.edu.dictionary.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.dictionary.service.AutocompleteService;

import java.util.List;

@Tag(name = "Autocomplete", description = "Word suggestions while typing")
@RestController
@RequestMapping("/autocomplete")
public class AutocompleteController {
    private final AutocompleteService service;

    public AutocompleteController(AutocompleteService service) {
        this.service = service;
    }

    @Operation(summary = "Suggest words by prefix")
    @GetMapping
    public List<String> suggest(
            @RequestParam String q
    ) {
        return service.suggest(q);
    }
}