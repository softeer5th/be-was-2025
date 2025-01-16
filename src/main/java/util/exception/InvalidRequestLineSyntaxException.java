package util.exception;

import http.constant.HttpStatus;

public class InvalidRequestLineSyntaxException extends RuntimeException {
    public final HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
    public InvalidRequestLineSyntaxException(String message) {
        super(message);
    }
}
