package webserver.request;


import webserver.enums.HttpMethod;
import webserver.enums.HttpVersion;
import webserver.exception.HttpVersionNotSupported;

import java.io.BufferedReader;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// HTTP 요청과 관련된 정보를 담는 객체
public class HttpRequest {
    private final HttpMethod httpMethod;
    private final RequestTarget requestTarget;
    private final HttpVersion version;
    private final Map<String, String> headers;
    // body를 읽어들이기 위한 Reader
    private final BufferedReader body;

    public HttpRequest(HttpMethod httpMethod, RequestTarget requestTarget, HttpVersion version, Map<String, String> headers, BufferedReader body) {
        this.httpMethod = httpMethod;
        this.requestTarget = requestTarget;
        this.version = version;
        this.headers = headers;
        this.body = body;
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

    public String getHeader(String key) {
        return headers.get(key);
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBodyAsString() {
        return body.lines().collect(Collectors.joining());
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
