package exception;

import enums.HttpStatus;

public class ClientErrorException extends RuntimeException {
    private final ErrorCode errorCode;

    public ClientErrorException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String getMessage() {
        return errorCode.getMessage();
    }

    public HttpStatus getHttpStatus(){
        return errorCode.getHttpStatus();
    }
}
