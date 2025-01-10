package http;

public enum HttpStatus {
    OK("200", "OK"),
    CREATED("201", "Created"),
    NOT_FOUND("404", "Not Found"),
    BAD_REQUEST("400", "Bad Request"),
    UNAUTHORIZED("401", "Unauthorized"),
    CONFLICT("409", "Conflict"),
    INTERNAL_SERVER_ERROR("500", "Internal Server Error");

    private final String code;
    private final String status;

    HttpStatus(String code, String status) {
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public String getStatus() {
        return status;
    }
}