package util.exception;

import http.constant.HttpStatus;

public class ArticleNotFoundException extends RuntimeException {
    public final HttpStatus httpStatus = HttpStatus.NOT_FOUND;
    public ArticleNotFoundException(String message) {
        super(message);
    }
}
