package constant;

public enum HTTPCode {


    OK(200, "OK", "Request success"),
    FOUND(302, "Found", "Redirected to another resource"),
    BAD_REQUEST(400, "Bad Request", "Bad request"),
    SEE_OTHER(303, "See Other", "See Other"),
    UNAUTHORIZED(401, "Unauthorized", "Unauthorized"),
    FORBIDDEN(403, "Forbidden", "Forbidden"),
    NOT_FOUND(404, "Not Found", "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed", "Method not allowed"),
    ALREADY_EXIST_USER(409, "Conflict", "User already exists");


    final private int statusCode;
    final private String reasonPhrase;
    final private String responseBody;

    public int getStatusCode() {
        return this.statusCode;
    }
    public String getResponseBody() {
        return this.responseBody;
    }

    public String getReasonPhrase() {
        return this.reasonPhrase;
    }

    HTTPCode(int httpCode, String reasonPhrase, String responseBody) {
        this.statusCode = httpCode;
        this.reasonPhrase = reasonPhrase;
        this.responseBody = responseBody;
    }

}
