package webserver.exception;

import webserver.enumeration.HTTPStatusCode;

public class HTTPException extends RuntimeException {
    private final String cause;
    private final HTTPStatusCode statusCode;

    public HTTPException(String cause, String message, HTTPStatusCode statusCode) {
        super(message);
        this.cause = cause;
        this.statusCode = statusCode;
    }

    @Override
    public String toString() {
        return String.format("HTTP Exception(%s): %s caused by %s", statusCode, super.getMessage(), cause);
    }

    public HTTPStatusCode getStatusCode() {
        return statusCode;
    }

    public static class Builder {
        private String cause;
        private HTTPStatusCode statusCode;
        private String message;

        public Builder causedBy(Class<?> causeClass) {
            this.cause = causeClass.getName();
            return this;
        }

        public Builder causedBy(String cause) {
            this.cause = cause;
            return this;
        }

        public HTTPException notFound(String resourceName) {
            return new HTTPException(cause, String.format("%s is not found", resourceName), HTTPStatusCode.NOT_FOUND);
        }
        public HTTPException badRequest(String reason) {
            return new HTTPException(cause, reason, HTTPStatusCode.BAD_REQUEST);
        }
        public HTTPException internalServerError(String reason) {
            return new HTTPException(cause, reason, HTTPStatusCode.SERVER_ERROR);
        }
        public Builder statusCode(HTTPStatusCode statusCode) {
            this.statusCode = statusCode;
            return this;
        }
        public Builder message(String message) {
            this.message = message;
            return this;
        }
        public HTTPException build() {
            return new HTTPException(cause, message, statusCode);
        }
    }
}
