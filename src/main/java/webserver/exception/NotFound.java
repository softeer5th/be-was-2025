package webserver.exception;

import static webserver.enums.HttpStatusCode.NOT_FOUND;

public class NotFound extends HttpException {
    public NotFound(String message) {
        this(message, null);
    }

    public NotFound(String message, Throwable cause) {
        super(NOT_FOUND, message, cause);
    }
}
