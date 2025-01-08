package util;

public enum HttpStatus {
    OK(200, "OK"),
    NOT_FOUND(404, "NOT FOUND");

    int code;
    String message;

    HttpStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
