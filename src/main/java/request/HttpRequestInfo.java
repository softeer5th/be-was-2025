package request;

import enums.HttpMethod;
import enums.HttpVersion;

import java.util.Map;

public class HttpRequestInfo {
    private final HttpMethod method;
    private final String path;
    private final HttpVersion version;

    private final Map<String, String> headers;
    private final Object body;

    public HttpRequestInfo(HttpMethod method, String path, HttpVersion version, Map<String, String> headers, Object body) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.headers = headers;

        this.body = body;
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

    public String getHeaderValue(String headerName) {
        return headers.get(headerName);
    }

    public Object getBody() {
        return body;
    }

}
