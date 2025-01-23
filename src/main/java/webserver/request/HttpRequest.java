package webserver.request;


import webserver.enums.HttpMethod;
import webserver.enums.HttpVersion;
import webserver.exception.HttpVersionNotSupported;
import webserver.header.RequestHeader;
import webserver.session.HttpSession;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * HTTP 요청과 관련된 정보를 담는 객체
 */
public class HttpRequest {
    private final HttpMethod httpMethod;
    private final RequestTarget requestTarget;
    private final HttpVersion version;
    private final RequestHeader headers;

    private final RequestBody bodyParser;
    private Map<String, String> pathVariables;
    private HttpSession session;

    /**
     * HttpRequest 생성자
     *
     * @param httpMethod    HTTP 메서드
     * @param requestTarget 요청 Path와 Query
     * @param version       HTTP 버전
     * @param headers       요청 헤더
     * @param bodyParser    요청 바디 파서
     */
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

    public Multipart getMultipart() {
        return bodyParser.getMultipart();
    }

    /**
     * 요청 헤더에 있는 HTTP 버전이 지원하는 버전인지 확인한다.
     *
     * @param supportedVersions 지원하는 HTTP 버전 목록
     * @throws HttpVersionNotSupported 지원하지 않는 HTTP 버전인 경우
     */
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

    /**
     * 세션을 설정한다.
     * SessionInterceptor.preHandle 에서 세션을 찾아 넣어줄 때 에서 사용한다.
     *
     * @param session 세션
     */
    public void setSession(HttpSession session) {
        this.session = session;
    }
}
