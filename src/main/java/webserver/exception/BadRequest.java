package webserver.exception;

import static webserver.enums.HttpStatusCode.BAD_REQUEST;

public class BadRequest extends HttpException {
    public BadRequest(String message) {
        this(message, null);
    }

    public BadRequest(String message, Throwable cause) {
        super(BAD_REQUEST, message, cause);
    }
}
