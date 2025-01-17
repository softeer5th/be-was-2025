package enums;

public enum HttpStatus {
    OK(200, "Ok"),
    CREATED(201, "Created"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    FOUND(302, "Found"),
    SEE_OTHER(303, "See Other");


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
