package webserver.enums;


import webserver.exception.NotImplemented;

public enum HttpMethod {
    GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH;

    public static HttpMethod of(String method) {
        try {
            return HttpMethod.valueOf(method.toUpperCase());
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new NotImplemented("Unsupported HTTP Method", e);
        }
    }
}
