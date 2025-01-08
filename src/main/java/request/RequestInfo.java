package request;

import enums.HttpMethod;

public class RequestInfo {
    private final HttpMethod method;
    private final String path;

    public RequestInfo(HttpMethod method, String path) {
        this.method = method;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    @Override
    public String toString() {
        return
                "method = " + method + ", path = " + path;
    }
}
