package pl.edu.dictionary.model;

public enum Language {
    EN("en"),
    ES("es"),
    FR("fr"),
    DE("de");

    private final String code;

    Language(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
