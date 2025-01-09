package webserver.enumeration;

public enum HTTPMethod {
    GET, POST, PUT, DELETE;

    static HTTPMethod from(String method) {
        switch (method) {
            case "GET":
                return GET;
            case "POST":
                return POST;
            case "PUT":
                return PUT;
            case "DELETE":
                return DELETE;
            default:
                throw new IllegalArgumentException("Invalid HTTP method: " + method);
        }
    }
}
