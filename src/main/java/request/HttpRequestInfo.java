package request;

import enums.HttpMethod;
import enums.HttpVersion;

public class HttpRequestInfo {
    private final HttpMethod method;
    private final String path;
    private final HttpVersion version;

    public HttpRequestInfo(HttpMethod method, String path, HttpVersion version) {
        this.method = method;
        this.path = path;
        this.version = version;
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
