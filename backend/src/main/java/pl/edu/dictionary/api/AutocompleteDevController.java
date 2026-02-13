package pl.edu.dictionary.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.edu.dictionary.client.AutocompleteClient;

import java.util.List;

@Tag(name = "AutocompleteDev", description = "Dev-only autocomplete testing endpoints")
@RestController
@RequestMapping("/autocomplete/dev")
@Profile("dev")
public class AutocompleteDevController {
    private final AutocompleteClient client;

    public AutocompleteDevController(AutocompleteClient client) {
        this.client = client;
    }

    @Operation(summary = "Raw autocomplete (prefix-based)")
    @GetMapping("/prefix")
    public List<String> autocomplete(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return client.suggest(q, limit);
    }

    @Operation(summary = "Raw spellcheck (sounds-like)")
    @GetMapping("/spellcheck")
    public List<String> spellcheck(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit
    ) {
        return client.spellcheck(q, limit);
    }
}
