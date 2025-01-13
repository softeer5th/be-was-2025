package webserver.enums;


import webserver.exception.NotImplemented;

public enum HttpMethod {
    GET, HEAD, POST, PUT, DELETE, CONNECT, OPTIONS, TRACE, PATCH;

    public static HttpMethod of(String method) {
        try {
            // Method 이름은 case-sensitive (rfc9110#section-9.1)
            return HttpMethod.valueOf(method);
        } catch (NullPointerException | IllegalArgumentException e) {
            throw new NotImplemented("Unsupported HTTP Method", e);
        }
    }
}
