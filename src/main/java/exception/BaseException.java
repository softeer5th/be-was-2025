package exception;

import http.HttpStatus;

public class BaseException extends RuntimeException {

    private final String message;
    private final HttpStatus status;

    public BaseException(ErrorCode code) {
        message = code.getMessage();
        status = code.getStatus();
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatus() {
        return status;
    }

}
