package webserver;

public enum HTTPMethod {
    // 지원 가능한 method
    GET(true),
    POST(true),
    // 미지원 method
    PUT(false),
    DELETE(false),
    PATCH(false),
    HEAD(false),
    OPTIONS(false),
    TRACE(false),
    CONNECT(false);

    private final boolean isSupported;

    HTTPMethod(boolean isSupported) {
        this.isSupported = isSupported;
    }

    // 유효한 메서드인지 검증
    public static boolean isValid(String method) {
        for (HTTPMethod methods : HTTPMethod.values()) {
            if (methods.name().equalsIgnoreCase(method)) {
                return true;
            }
        }
        return false;
    }

    // 지원 가능한 메서드인지 검증
    public static boolean isSupported(String method) {
        for (HTTPMethod httpMethod : values()) {
            if (httpMethod.name().equalsIgnoreCase(method)) {
                return httpMethod.isSupported;
            }
        }
        return false;
    }
}