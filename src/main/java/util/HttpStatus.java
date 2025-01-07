package util;

public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201,"Created"),
    NOT_FOUND(404, "NOT_FOUND");


    private final int code;
    private final String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
