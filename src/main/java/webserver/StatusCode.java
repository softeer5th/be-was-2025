package webserver;

public enum StatusCode {
    OK(200, "OK"), NOT_FOUND(404, "Not Found");

    int code;
    String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
