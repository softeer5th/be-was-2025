package http;

public class HttpRequestInfo {

    private final HttpMethod method;
    private final String path;
    private final String body;

    public HttpRequestInfo(HttpMethod method, String url, String body) {
        this.method = method;
        this.path = url;
        this.body = body;
    }

    public String getPath() {
        return path;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public String getBody() {
        return body;
    }

}
