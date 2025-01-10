package http;

public enum HttpStatus {
    OK("200", "OK"),
    CREATED("201", "Created"),
    NOT_FOUND("404", "Not Found");

    private final String code;
    private final String status;

    HttpStatus(String code, String status) {
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }
}