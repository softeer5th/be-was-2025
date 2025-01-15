package exception;

import http.HttpStatus;

public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND_FOR_SESSION(HttpStatus.UNAUTHORIZED, "세션에 해당하는 사용자를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    UserErrorCode(HttpStatus httpStatus, String message) {
        this.status = httpStatus;
        this.message = message;
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }

}
