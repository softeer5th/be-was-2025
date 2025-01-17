package exception;

public class MissingUserInfoException extends RuntimeException {
    public MissingUserInfoException(String message) {
        super(message);
    }
}
