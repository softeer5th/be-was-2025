package util.exception;

import http.constant.HttpStatus;

public class NotAllowedMethodException extends RuntimeException {
    public final HttpStatus httpStatus = HttpStatus.METHOD_NOT_ALLOWED;
    public NotAllowedMethodException(String message) {
        super(message);
    }
    public NotAllowedMethodException() {}
}
