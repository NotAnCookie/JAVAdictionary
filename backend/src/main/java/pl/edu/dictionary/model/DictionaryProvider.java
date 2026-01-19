package pl.edu.dictionary.model;

public enum DictionaryProvider {
    MOCK_DICTIONARY("mockDictionaryClient"),
    DICTIONARY_API_DEV("dictionaryApiDevClient"),
    DATAMUSE("datamuseClient"),
    FREE_DICTIONARY("freeDictionaryClient");

    private final String beanName;

    DictionaryProvider(String beanName) {
        this.beanName = beanName;
    }

    public String getBeanName() {
        return beanName;
    }
}
