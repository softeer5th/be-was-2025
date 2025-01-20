package exception;

import enums.HttpStatus;

public class ErrorException extends RuntimeException {

    private final ErrorCode errorCode;


    public ErrorException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }

    public HttpStatus getHttpStatus() {
        return errorCode.getHttpStatus();
    }
}
