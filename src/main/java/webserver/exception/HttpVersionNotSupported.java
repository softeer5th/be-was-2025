package webserver.exception;

import static webserver.enums.HttpStatusCode.HTTP_VERSION_NOT_SUPPORTED;

public class HttpVersionNotSupported extends HttpException {
    public HttpVersionNotSupported(String message) {
        this(message, null);
    }

    public HttpVersionNotSupported(String message, Throwable cause) {
        super(HTTP_VERSION_NOT_SUPPORTED, message, cause);
    }
}
