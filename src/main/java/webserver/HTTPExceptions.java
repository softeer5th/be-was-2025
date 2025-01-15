package webserver;

public class HTTPExceptions extends RuntimeException {
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
            super(message, 400);
        }
    }

    public static class Error404 extends HTTPExceptions {
        public Error404(String message) {
            super(message, 404);
        }
    }

    public static class Error405 extends HTTPExceptions {
        public Error405(String message) {
            super(message, 405);
        }
    }

    public static class Error409 extends HTTPExceptions {
        public Error409(String message) {
            super(message, 409);
        }
    }

    public static class Error415 extends HTTPExceptions {
        public Error415(String message) {
            super(message, 415);
        }
    }

    public static class Error505 extends HTTPExceptions {
        public Error505(String message) {
            super(message, 505);
        }
    }

    public static byte[] getErrorMessage(String message) {
        return message.getBytes();
    }
}