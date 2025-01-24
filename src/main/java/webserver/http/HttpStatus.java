package webserver.http;

public enum HttpStatus {
    OK(200, "OK"),
    CREATED(201, "Created"),
    NO_CONTENT(204, "No Content"),

    FOUND(302, "Found"),

    BAD_REQUEST(400, "Bad Request"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),

    INTERNAL_SERVER_ERROR(500, "Internal Server Error"),
    ;

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

    public static HttpStatus of(int code) {
        for (HttpStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown HTTP status code: " + code);
    }

    public static HttpStatus of(String message) {
        for (HttpStatus status : values()) {
            if (status.message.equalsIgnoreCase(message)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown HTTP status message: " + message);
    }
}
