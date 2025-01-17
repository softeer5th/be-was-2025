package exception;

import enums.HttpStatus;

import static enums.HttpStatus.*;

public enum ErrorCode {
    // <------------------- 5XX Error Code ------------------->
    KEY_VALUE_SHOULD_BE_EVEN(INTERNAL_SERVER_ERROR,"header는 key-value 쌍으로 이루어져야 합니다."),
    ERROR_WITH_ENCODING(INTERNAL_SERVER_ERROR,"인코딩에 실패하였습니다."),

    // <------------------- 4XX Error Code ------------------->
    INVALID_FORM(BAD_REQUEST, "요청 폼이 잘못되었습니다."),
    MISSING_FIELD(BAD_REQUEST, "누락된 필드가 있습니다"),
    UNSUPPORTED_HTTP_VERSION(HTTP_VERSION_NOT_SUPPORTED, "지원하지 않는 http 요청입니다."),
    INVALID_HTTP_METHOD(HttpStatus.METHOD_NOT_ALLOWED,"잘못된 HTTP 메소드입니다."),
    INVALID_HTTP_REQUEST(BAD_REQUEST, "잘못된 HTTP 요청입니다."),
    REQUEST_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED,"허용하지 않는 HTTP 요청입니다."),
    UNSUPPORTED_FILE_EXTENSION(UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 파일 확장자 형식입니다"),
    FILE_NOT_FOUND(BAD_REQUEST, "존재하지 않는 파일입니다."),
    NOT_ALLOWED_PATH(BAD_REQUEST, "잘못된 요청입니다."),
    ALREADY_EXIST_USERID(BAD_REQUEST,"이미 존재하는 userId입니다."),
    NO_SUCH_USER_ID(BAD_REQUEST,"존재하지 않는 userId입니다."),
    INCORRECT_PASSWORD(BAD_REQUEST, "비밀번호가 일치하지 않습니다.");

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
