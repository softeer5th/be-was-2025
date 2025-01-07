package enums;

public enum HttpMethod {
    GET,
    POST;

    public static HttpMethod match(String method) {
        return switch (method) {
            case "GET" -> GET;
            case "POST" -> POST;
            default -> throw new IllegalArgumentException("잘못된 http method입니다.");
        };
    }
}
