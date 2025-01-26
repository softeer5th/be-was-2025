package exception;

import http.HttpStatus;

public enum SessionErrorCode implements ErrorCode {
    USER_NOT_FOUND_FOR_SESSION(HttpStatus.UNAUTHORIZED, "세션에 해당하는 사용자를 찾을 수 없습니다."),
    MISSING_SESSION(HttpStatus.UNAUTHORIZED, "세션 정보가 없습니다."),
    EMPTY_SESSION(HttpStatus.UNAUTHORIZED, "세션 값이 비어있습니다."),
    INVALID_SESSION(HttpStatus.UNAUTHORIZED, "유효하지 않은 세션입니다.");

    private final HttpStatus status;
    private final String message;

    SessionErrorCode(HttpStatus httpStatus, String message) {
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
