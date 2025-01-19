package webserver.enumeration;

public enum HTTPStatusCode {
    OK(200, "OK"),
    CREATED(201, "Created"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    NOT_FOUND(404, "Not Found"),
    UNAUTHORIZED(401, "Unauthorized"),
    FORBIDDEN(403, "Forbidden"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed"),
    NOT_ACCEPTABLE(406, "Not Acceptable"),
    LENGTH_REQUIRED(411, "Length Required"),
    UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    SERVER_ERROR(500, "Internal Server Error");

    final int code;
    final String description;

    HTTPStatusCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public String toString() {
        return code + " " + description;
    }
}
