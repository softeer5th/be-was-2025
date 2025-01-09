package enums;

public enum HttpStatus {
    OK(200, "Ok"),
    CREATED(201, "Created"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Unsupported Media Type"),
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
