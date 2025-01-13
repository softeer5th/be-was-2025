package webserver.response;

import webserver.common.HttpHeaders;
import webserver.enums.HttpHeader;
import webserver.enums.HttpStatusCode;
import webserver.response.body.Body;

import java.io.File;

// HTTP 응답에 대한 정보를 담는 객체
public class HttpResponse {
    private final HttpStatusCode statusCode;
    private final HttpHeaders headers;
    private Body body;

    public HttpResponse(HttpStatusCode statusCode) {
        this(statusCode, new HttpHeaders());
    }

    public HttpResponse(HttpStatusCode statusCode, HttpHeaders headers) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = Body.empty();
    }

    public static HttpResponse redirect(String location) {
        return new HttpResponse(HttpStatusCode.SEE_OTHER)
                .setHeader(HttpHeader.LOCATION.value, location);
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }

    Body getBody() {
        return body;
    }

    public HttpResponse setBody(String string) {
        if (string == null)
            this.body = Body.empty();
        else
            this.body = Body.of(string);
        setContentHeaders();
        return this;
    }

    public HttpResponse setBody(File file) {
        if (file == null)
            this.body = Body.empty();
        else
            this.body = Body.of(file);
        setContentHeaders();
        return this;
    }

    public HttpResponse setHeader(String key, String value) {
        headers.setHeader(key, value);
        return this;
    }

    private void setContentHeaders() {
        body.getContentLength()
                .ifPresent(contentLength -> headers.setHeader(HttpHeader.CONTENT_LENGTH, contentLength.toString()));
        body.getContentType()
                .ifPresent(contentType -> headers.setHeader(HttpHeader.CONTENT_TYPE, contentType.mimeType));

    }

    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusCode=" + statusCode +
                ", headers=" + headers +
                ", body=" + body +
                '}';
    }
}
