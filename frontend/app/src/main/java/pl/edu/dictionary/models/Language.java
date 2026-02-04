package pl.edu.dictionary.models;

public enum Language {
    EN("EN"),
    ES("ES"),
    FR("FR"),
    DE("DE");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
