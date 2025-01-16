package util.exception;

import http.constant.HttpStatus;

public class SessionNotFoundException extends RuntimeException {
    public final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    public SessionNotFoundException(String message) {
        super(message);
    }
}
