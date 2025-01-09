package request;

public enum HttpStatus {
    OK("200"),
    NOT_FOUND("404");

    private final String code;

    HttpStatus(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public String getFormattedName() {
        return this.name().replace("_", " ");
    }
}