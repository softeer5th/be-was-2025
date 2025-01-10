package webserver.exception;

import static webserver.enums.HttpStatusCode.CONFLICT;

public class Conflict extends HttpException {
    public Conflict(String message) {
        this(message, null);
    }

    public Conflict(String message, Throwable cause) {
        super(CONFLICT, message, cause);
    }
}
