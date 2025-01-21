package util.exception;

import http.constant.HttpStatus;

public class UserNotFoundException extends RuntimeException{
    public final HttpStatus httpStatus = HttpStatus.NOT_FOUND;
    public UserNotFoundException(String message) {
        super(message);
    }
}
