package pl.edu.dictionary.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DictionaryClientFactory {

    private final Map<String, DictionaryClient> clients;

    public DictionaryClientFactory(Map<String, DictionaryClient> clients) {
        this.clients = clients;
    }

    public DictionaryClient getClient(String provider) {
        DictionaryClient client = clients.get(provider);
        if (client == null) {
            throw new IllegalArgumentException("Unknown dictionary provider: " + provider);
        }
        return client;
    }

    public DictionaryClient getDefaultClient() {
        return clients.get("dictionaryApiDevClient");
    }
}

