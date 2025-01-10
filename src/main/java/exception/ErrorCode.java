package exception;

import http.HttpStatus;

public interface ErrorCode {

    HttpStatus getStatus();

    String getMessage();

}
