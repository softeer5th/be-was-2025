package exception;

public class NotExistApiRequestException extends RuntimeException{
    public NotExistApiRequestException(String message) {
        super(message);
    }
}
