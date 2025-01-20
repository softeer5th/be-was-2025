package exception;

public class ClientErrorException extends ErrorException {

    public ClientErrorException(ErrorCode errorCode) {
      super(errorCode);
    }
}
