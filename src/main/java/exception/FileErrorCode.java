package exception;

import http.HttpStatus;

public enum FileErrorCode implements ErrorCode {

    UNSUPPORTED_FILE_EXTENSION(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 파일 확장자 형식입니다"),

    FILE_NOT_FOUND(HttpStatus.BAD_REQUEST, "존재하지 않는 파일입니다.");

    private final HttpStatus status;
    private final String message;

    FileErrorCode(HttpStatus httpStatus, String message) {
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
