package enums;

public enum HttpStatus {
    OK(200, "ok"),
    CREATED(201, "created"),
    BAD_REQUEST(400, "bad request"),
    NOT_FOUND(404, "not found"),
    UNSUPPORTED_MEDIA_TYPE(415, "unsupported media type");


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
