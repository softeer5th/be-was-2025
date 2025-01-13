package webserver.request;


import webserver.common.HttpHeaders;
import webserver.enums.HttpMethod;
import webserver.enums.HttpVersion;
import webserver.exception.HttpVersionNotSupported;

import java.util.List;
import java.util.Map;

// HTTP 요청과 관련된 정보를 담는 객체
public class HttpRequest {
    private final HttpMethod httpMethod;
    private final RequestTarget requestTarget;
    private final HttpVersion version;
    private final HttpHeaders headers;
    // body를 읽어들이기 위한 Reader
    private final byte[] body;
    private Map<String, String> pathVariables;

    public HttpRequest(HttpMethod httpMethod, RequestTarget requestTarget, HttpVersion version, HttpHeaders headers, byte[] body) {
        this.httpMethod = httpMethod;
        this.requestTarget = requestTarget;
        this.version = version;
        this.headers = headers;
        this.body = body;
        this.pathVariables = Map.of();
    }

    public void setPathVariables(Map<String, String> pathVariables) {
        this.pathVariables = pathVariables;
    }

    public String getPathVariable(String key) {
        return pathVariables.get(key);
    }

    public HttpMethod getMethod() {
        return httpMethod;
    }

    public RequestTarget getRequestTarget() {
        return requestTarget;
    }

    public HttpVersion getVersion() {
        return version;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    // request body를 읽어들여 문자열로 반환
    public String readBodyAsString() {
        return new String(body);
    }

    public void validateSupportedHttpVersion(List<HttpVersion> supportedVersions) {
        if (!supportedVersions.contains(version)) {
            throw new HttpVersionNotSupported("Unsupported HTTP version: " + version);
        }
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "method=" + httpMethod +
                ", requestTarget=" + requestTarget +
                ", version=" + version +
                ", headers=" + headers +
                '}';
    }

}
