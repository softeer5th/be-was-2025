package http;

public class HttpRequestInfo {

    private final HttpMethod method;
    private final String path;

    public HttpRequestInfo(HttpMethod method, String url) {
        this.method = method;
        this.path = url;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }


}
