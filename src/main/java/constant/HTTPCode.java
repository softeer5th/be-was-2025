package constant;

public enum HTTPCode {


    OK(200, "Success"),
    FOUND(302, "Found"),
    BAD_REQUEST(400, "Bad Request"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    METHOD_NOT_ALLOWED(405, "Method Not Allowed");


    final private int HTTPCode;
    final private String message;

    public int getHTTPCode() {
        return this.HTTPCode;
    }
    public String getMessage() {
        return this.message;
    }

    private HTTPCode(int httpCode, String message) {
        this.HTTPCode = httpCode;
        this.message = message;
    }

}
