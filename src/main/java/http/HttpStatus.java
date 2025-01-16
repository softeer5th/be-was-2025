package http;

public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),
    FOUND(302, "Found"),
    SEE_OTHER(303, "See Other"),
    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    NOT_FOUND(404, "Unsupported Media Type"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    UNSUPPORTED_MEDIA_TYPE(415, "unsupported media type"),
    INTERNAL_SERVER_ERROR(500, "Internal Server Error");

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
