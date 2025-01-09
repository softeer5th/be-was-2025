package exception;

public class UserCreationException extends RuntimeException {
    private final ErrorCode errorCode;
    public UserCreationException(ErrorCode errorCode) {
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