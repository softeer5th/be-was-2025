package exception;

import enums.HttpStatus;

import static enums.HttpStatus.BAD_REQUEST;
import static enums.HttpStatus.UNSUPPORTED_MEDIA_TYPE;

public enum ErrorCode {
    INVALID_FORM(BAD_REQUEST, "요청 폼이 잘못되었습니다."),
    MISSING_FIELD(BAD_REQUEST, "누락된 필드가 있습니다"),
    UNSUPPORTED_HTTP_VERSION(BAD_REQUEST, "지원하지 않는 http 요청입니다."),
    INVALID_HTTP_REQUEST(BAD_REQUEST, "잘못된 HTTP 요청입니다."),
    UNSUPPORTED_FILE_EXTENSION(UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 파일 확장자 형식입니다"),
    FILE_NOT_FOUND(BAD_REQUEST, "존재하지 않는 파일입니다."),
    NOT_ALLOWED_PATH(BAD_REQUEST, "잘못된 요청입니다.");

    private final HttpStatus httpStatus;
    private final String message;

    ErrorCode(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getMessage() {
        return message;
    }
}
