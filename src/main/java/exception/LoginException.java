package exception;

public class LoginException extends ClientErrorException {

    public LoginException(ErrorCode errorCode) {
        super(errorCode);
    }
}
