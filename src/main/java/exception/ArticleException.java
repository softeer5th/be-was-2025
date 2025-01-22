package exception;

public class ArticleException extends RuntimeException{
    private ErrorCode errorCode;
    public ArticleException(ErrorCode errorCode) {
        super(errorCode.getMessage());
    }
}
