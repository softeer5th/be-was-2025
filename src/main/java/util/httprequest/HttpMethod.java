package util.httprequest;

import java.util.HashMap;
import java.util.Map;

public enum HttpMethod {
    GET("GET"),
    POST("POST"),
    PUT("PUT"),
    DELETE("DELETE"),
    PATCH("PATCH"),
    HEAD("HEAD"),
    OPTIONS("OPTIONS"),
    INVALID("INVALID");

    private final String method;

    private static final Map<String, HttpMethod> HTTP_METHOD_MAP = new HashMap<>();

    static {
        for (HttpMethod httpMethod : values()) {
            HTTP_METHOD_MAP.put(httpMethod.method, httpMethod);
        }
    }

    HttpMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return this.method;
    }

    public static HttpMethod getMethodFromString(String method) {
        HttpMethod httpMethod = HTTP_METHOD_MAP.get(method.toUpperCase());
        return httpMethod != null ? httpMethod : INVALID;
    }
}
