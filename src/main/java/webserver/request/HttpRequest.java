package webserver.request;


import webserver.enums.HttpMethod;
import webserver.enums.HttpVersion;
import webserver.exception.HttpVersionNotSupported;
import webserver.header.RequestHeader;
import webserver.session.HttpSession;

import java.util.List;
import java.util.Map;
import java.util.Optional;

// HTTP 요청과 관련된 정보를 담는 객체
public class HttpRequest {
    private final HttpMethod httpMethod;
    private final RequestTarget requestTarget;
    private final HttpVersion version;
    private final RequestHeader headers;

    private final RequestBody bodyParser;
    private Map<String, String> pathVariables;
    private HttpSession session;

    public HttpRequest(HttpMethod httpMethod, RequestTarget requestTarget, HttpVersion version, RequestHeader headers, RequestBody bodyParser) {
        this.httpMethod = httpMethod;
        this.requestTarget = requestTarget;
        this.version = version;
        this.headers = headers;
        this.bodyParser = bodyParser;
        this.pathVariables = Map.of();
    }

    public void setPathVariables(Map<String, String> pathVariables) {
        this.pathVariables = pathVariables;
    }

    public Optional<String> getPathVariable(String key) {
        return Optional.ofNullable(pathVariables.get(key));
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

    public RequestHeader getHeaders() {
        return headers;
    }

    public <T> Optional<T> getBody(Class<T> clazz) {
        return bodyParser.getBody(clazz);
    }

    public void validateSupportedHttpVersion(List<HttpVersion> supportedVersions) {
        if (!supportedVersions.contains(version)) {
            throw new HttpVersionNotSupported("Unsupported HTTP version: " + version);
        }
    }


    @Override
    public String toString() {
        return "HttpRequest{" +
               "httpMethod=" + httpMethod +
               ", requestTarget=" + requestTarget +
               ", version=" + version +
               ", headers=" + headers +
               ", pathVariables=" + pathVariables +
               '}';
    }

    public HttpSession getSession() {
        return session;
    }

    public void setSession(HttpSession session) {
        this.session = session;
    }
}
