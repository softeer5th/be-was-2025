package exception;

public class ServerErrorException extends ErrorException {

    public ServerErrorException(ErrorCode errorCode) {
        super(errorCode);
    }


}
