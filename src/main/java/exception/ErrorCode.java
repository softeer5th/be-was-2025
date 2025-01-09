package exception;

public enum ErrorCode {
    INVALID_FORM("요청 폼이 잘못되었습니다."),
    INVALID_HTTP_REQUEST("잘못된 HTTP 요청입니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
