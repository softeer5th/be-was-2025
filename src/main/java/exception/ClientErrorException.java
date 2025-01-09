package exception;

public class ClientErrorException extends RuntimeException {
    private final ErrorCode message;

    public ClientErrorException(ErrorCode message) {
        this.message = message;
    }
}
