package exception;

import http.HttpStatus;

public enum UserErrorCode implements ErrorCode {

    USER_NOT_FOUND_FOR_SESSION(HttpStatus.UNAUTHORIZED, "세션에 해당하는 사용자를 찾을 수 없습니다."),
    INVALID_USER_ID(HttpStatus.BAD_REQUEST, "잘못된 ID 형식입니다. (영문, 숫자 조합 4~16자)"),
    INVALID_NICKNAME(HttpStatus.BAD_REQUEST, "잘못된 닉네임 형식입니다. (한글, 영문, 숫자 2~10자)"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "비밀번호는 8~20자이며, 영문, 숫자, 특수문자를 포함해야 합니다."),
    INVALID_EMAIL(HttpStatus.BAD_REQUEST, "잘못된 이메일 형식입니다.");
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
