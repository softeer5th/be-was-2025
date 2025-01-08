package webserver.response;

import webserver.enums.HttpHeader;
import webserver.enums.HttpStatusCode;
import webserver.response.body.Body;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

// HTTP 응답에 대한 정보를 담는 객체
public class HttpResponse {
    private final HttpStatusCode statusCode;
    private final Map<String, String> headers;
    private Body body;

    public HttpResponse(HttpStatusCode statusCode) {
        this(statusCode, new HashMap<>());
    }

    public HttpResponse(HttpStatusCode statusCode, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = Body.empty();
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
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
        headers.put(key, value);
        return this;
    }

    private void setContentHeaders() {
        headers.put(HttpHeader.CONTENT_LENGTH.value, body.getContentLength().toString());
        body.getContentType()
                .ifPresent(contentType -> headers.put(HttpHeader.CONTENT_TYPE.value, contentType.mimeType));

    }


}
