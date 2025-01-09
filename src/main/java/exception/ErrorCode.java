package exception;

public enum ErrorCode {
    INVALID_FORM("요청 폼이 잘못되었습니다."),
    MISSING_FIELD("누락된 필드가 있습니다"),
    INVALID_HTTP_REQUEST("잘못된 HTTP 요청입니다."),
    FILE_NOT_FOUND("존재하지 않는 파일입니다."),
    NOT_ALLOWED_PATH("잘못된 요청입니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
