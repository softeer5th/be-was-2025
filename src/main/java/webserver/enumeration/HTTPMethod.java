package webserver.enumeration;

import webserver.exception.HTTPException;

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
                throw new HTTPException.Builder()
                        .causedBy("HTTPMethod from")
                        .badRequest("Invalid HTTP method: " + method);
        }
    }
}
