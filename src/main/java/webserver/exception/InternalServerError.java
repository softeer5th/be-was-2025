package webserver.exception;

import static webserver.enums.HttpStatusCode.INTERNAL_SERVER_ERROR;

public class InternalServerError extends HttpException {
    public InternalServerError(String message) {
        this(message, null);
    }

    public InternalServerError(String message, Throwable cause) {
        super(INTERNAL_SERVER_ERROR, message, cause);
    }
}
