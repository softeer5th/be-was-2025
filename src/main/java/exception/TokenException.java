package exception;

public class TokenException extends RuntimeException {
    private final ErrorCode errorCode;
    public TokenException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String getMessage() {
        return "[%s] %s".formatted(errorCode.name(), errorCode.getMessage());
    }
}