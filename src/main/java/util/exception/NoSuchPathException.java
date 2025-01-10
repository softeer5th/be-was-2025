package util.exception;

import http.HttpStatus;

public class NoSuchPathException extends RuntimeException{
    public final HttpStatus httpStatus = HttpStatus.NOT_FOUND;
    public NoSuchPathException(String message) {
        super(message);
    }
    public NoSuchPathException() {
    }
}
