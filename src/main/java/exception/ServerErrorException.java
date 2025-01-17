package exception;

import enums.HttpStatus;

public class ServerErrorException extends RuntimeException {
    private final ErrorCode errorCode;

    public ServerErrorException(ErrorCode errorCode) {
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
