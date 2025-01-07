package webserver.enums;


import webserver.exception.BadRequest;

public enum HttpMethod {
    GET, POST, PUT, DELETE, PATCH;

    public static HttpMethod of(String method) {
        try {
            return HttpMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequest("Unsupported HTTP Method", e);
        }
    }
}
