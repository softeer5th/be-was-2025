package exception;

import http.HttpStatus;

public enum HttpErrorCode implements ErrorCode {

    INVALID_HTTP_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    NOT_FOUND_PATH(HttpStatus.NOT_FOUND, "경로를 찾을 수 없습니다."),
    INVALID_QUERY_PARAM(HttpStatus.BAD_REQUEST, "잘못된 쿼리 파라미터 형식입니다."),
    INVALID_HTTP_METHOD(HttpStatus.BAD_REQUEST, "잘못된 Http method 입니다.");

    private final HttpStatus status;
    private final String message;

    HttpErrorCode(HttpStatus httpStatus, String message) {
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
