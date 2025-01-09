package webserver.request;


import webserver.enums.HttpMethod;
import webserver.enums.HttpVersion;
import webserver.exception.BadRequest;
import webserver.exception.HttpVersionNotSupported;
import webserver.exception.InternalServerError;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import static webserver.enums.HttpHeader.CONTENT_LENGTH;

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

    // request body를 읽어들여 문자열로 반환
    public String readBodyAsString() {
        String contentLengthString = headers.get(CONTENT_LENGTH.value);
        if (contentLengthString == null)
            throw new BadRequest(CONTENT_LENGTH.value + "헤더가 없습니다.");

        try {
            int contentLength = Integer.parseInt(contentLengthString);
            char[] buffer = new char[contentLength];
            int remainBytes = contentLength;
            // // Contetnt-Length 크기만큼 읽어들임
            while (remainBytes > 0) {
                int readBytes = body.read(buffer, contentLength - remainBytes, remainBytes);
                if (readBytes == -1) {
                    throw new BadRequest(CONTENT_LENGTH.value + "값이 올바르지 않습니다.");
                }
                remainBytes -= readBytes;
            }
            return new String(buffer);
        } catch (NumberFormatException e) {
            throw new BadRequest(CONTENT_LENGTH.value + "값이 올바르지 않습니다.");
        } catch (IOException e) {
            throw new InternalServerError("요청 Body를 읽는 중 오류가 발생했습니다.");
        }

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
