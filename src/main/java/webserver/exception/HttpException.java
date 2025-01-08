package webserver.exception;


import webserver.enums.HttpStatusCode;

public class HttpException extends RuntimeException {
    private final int statusCode;

    public HttpException(HttpStatusCode statusCode, String message) {
        this(statusCode, message, null);
    }

    public HttpException(int statusCode, String message) {
        this(statusCode, message, null);
    }

    public HttpException(HttpStatusCode statusCode, String message, Throwable cause) {
        this(statusCode.statusCode, message, cause);
    }

    public HttpException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }


    public int getStatusCode() {
        return statusCode;
    }
}
