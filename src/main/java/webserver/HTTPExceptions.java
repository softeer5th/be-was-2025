package webserver;

public class HTTPExceptions {
    public static class Error400 extends RuntimeException {
        public Error400(String message) {
            super(message);
        }
    }

    public static class Error404 extends RuntimeException {
        public Error404(String message) {
            super(message);
        }
    }

    public static class Error405 extends RuntimeException {
        public Error405(String message) {
            super(message);
        }
    }

    public static class Error505 extends RuntimeException {
        public Error505(String message) {
            super(message);
        }
    }
}
