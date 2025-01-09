package exception;

public enum ErrorCode {
    INVALID_FORM("요청 폼이 잘못되었습니다.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }
}
