package webserver.exception;

import static webserver.enums.HttpStatusCode.NOT_IMPLEMENTED;

public class NotImplemented extends HttpException {
    public NotImplemented(String message) {
        this(message, null);
    }

    public NotImplemented(String message, Throwable cause) {
        super(NOT_IMPLEMENTED, message, cause);
    }
}
