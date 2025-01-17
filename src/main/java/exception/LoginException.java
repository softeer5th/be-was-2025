package exception;

public class LoginException extends RuntimeException {
    private ErrorCode errorCode;
    public LoginException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
