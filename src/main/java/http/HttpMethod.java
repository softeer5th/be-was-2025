package http;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod match(String method) {
        return switch (method) {
            case "get" -> GET;
            case "post" -> POST;
            default -> throw new IllegalArgumentException("잘못된 http method 입니다.");
        };
    }
}
