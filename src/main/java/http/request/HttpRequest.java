package http.request;

import http.enums.HttpMethod;
import http.enums.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpRequest {
    private HttpMethod method;
    private TargetInfo target;
    private HttpVersion version;
    private String headers;
    private String body;

    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    public HttpRequest(HttpMethod method, TargetInfo target, HttpVersion version, String headers, String body) {
        this.method = method;
        this.target = target;
        this.version = version;
        this.headers = headers;
        this.body = body;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public TargetInfo getTarget() {
        return target;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public String getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }

    public boolean isInvalid() {
        return method == HttpMethod.INVALID && target == null && version == HttpVersion.INVALID;
    }
}
