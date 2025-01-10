package exception;

public class SignUpException extends RuntimeException{
    private ErrorCode errorCode;

    public SignUpException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
