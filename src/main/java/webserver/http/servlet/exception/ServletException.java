package webserver.http.servlet.exception;

import webserver.http.HttpStatus;

public class ServletException extends RuntimeException {
    private HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;

    public ServletException(String message) { super(message); }

    public ServletException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() { return status; }
}
