package webserver.enums;


public enum HttpMethod {
    GET, POST, PUT, DELETE, PATCH;

    public static HttpMethod of(String method) {
        try {
            return HttpMethod.valueOf(method.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Unsupported HTTP Method");
        }
    }
}
