package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTTPExceptions extends RuntimeException {
    private static final Logger logger = LoggerFactory.getLogger(HTTPExceptions.class);
    private final int statusCode;

    public HTTPExceptions(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public static class Error400 extends HTTPExceptions {
        public Error400(String message) {
            super("400 Bad Request", 400);
            logger.error("400 Bad Request: {}", message);
        }
    }

    public static class Error403 extends HTTPExceptions {
        public Error403(String message) {
            super("403 Forbidden", 403);
            logger.error("403 Forbidden: {}", message);
        }
    }

    public static class Error404 extends HTTPExceptions {
        public Error404(String message) {
            super("404 not found", 404);
            logger.error("404 not found: {}", message);
        }
    }

    public static class Error405 extends HTTPExceptions {
        public Error405(String message) {
            super("405 Method Not Allowed", 405);
            logger.error("405 Method Not Allowed: {}", message);
        }
    }

    public static class Error409 extends HTTPExceptions {
        public Error409(String message) {
            super("409 Conflict", 409);
            logger.error("409 Conflict: {}", message);
        }
    }

    public static class Error415 extends HTTPExceptions {
        public Error415(String message) {
            super("405 Unsupported Media Type", 415);
            logger.error("415 Unsupported Media Type: {}", message);
        }
    }

    public static class Error500 extends HTTPExceptions {
        public Error500(String message) {
            super("500 Internal Server Error", 500);
            logger.error("500 Internal Server Error: {}", message);
        }
    }

    public static class Error505 extends HTTPExceptions {
        public Error505(String message) {
            super("505 HTTP Version Not Supported", 505);
            logger.error("505 HTTP Version Not Supported: {}", message);
        }
    }

    public static byte[] getErrorMessageToBytes(String message) {
        return message.getBytes();
    }
}