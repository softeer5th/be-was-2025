package global.exception;

public enum ErrorCode {
    USER_ALREADY_EXISTS("SIGNUP-01", "사용자가 이미 존재합니다."),
    DUPLICATED_NAME("SIGNUP-02", "이미 사용 중인 닉네임입니다."),
    INVALID_USER_INPUT("SIGNUP-03","유효하지 않은 사용자 입력입니다."),
    INTERNAL_ERROR("SERVER-ERROR","내부 서버 오류가 발생했습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}