package exception;

public enum ErrorCode {
    INVALID_USER_INPUT("유효하지 않은 사용자 입력입니다."),
    USER_ALREADY_EXISTS("사용자가 이미 존재합니다."),
    USER_NOT_FOUND("사용자를 찾을 수 없습니다."),
    INTERNAL_ERROR("내부 서버 오류가 발생했습니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}