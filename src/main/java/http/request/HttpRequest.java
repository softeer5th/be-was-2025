package http.request;

import http.enums.HttpMethod;
import http.enums.HttpVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class HttpRequest {
    private HttpMethod method;
    private TargetInfo target;
    private HttpVersion version;
    private Map<String, String> headers;
    private byte[] body;

    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    public HttpRequest(HttpMethod method, TargetInfo target, HttpVersion version, Map<String, String> headers, byte[] body) {
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

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public boolean isInvalid() {
        return method == HttpMethod.INVALID && target == null && version == HttpVersion.INVALID;
    }
}
