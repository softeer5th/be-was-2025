package webserver.response;

import webserver.enums.HttpHeader;
import webserver.enums.HttpStatusCode;
import webserver.header.ResponseHeader;
import webserver.header.SetCookie;
import webserver.response.body.ResponseBody;

import java.io.File;

// HTTP 응답에 대한 정보를 담는 객체
public class HttpResponse {
    private final HttpStatusCode statusCode;
    private final ResponseHeader headers;
    private ResponseBody body;

    public HttpResponse(HttpStatusCode statusCode) {
        this(statusCode, new ResponseHeader());
    }

    public HttpResponse(HttpStatusCode statusCode, ResponseHeader headers) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = ResponseBody.empty();
    }

    public static HttpResponse redirect(String location) {
        return new HttpResponse(HttpStatusCode.FOUND)
                .setHeader(HttpHeader.LOCATION.value, location);
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public ResponseHeader getHeaders() {
        return headers;
    }

    public HttpResponse setHeader(String key, String value) {
        headers.setHeader(key, value);
        return this;
    }

    public void setCookie(String name, String value) {
        headers.addSetCookie(new SetCookie(name, value));
    }

    public void deleteCookie(String name) {
        SetCookie setCookie = new SetCookie(name, "");
        setCookie.setMaxAge(0);
        headers.addSetCookie(setCookie);
    }


    @Override
    public String toString() {
        return "HttpResponse{" +
                "statusCode=" + statusCode +
                ", headers=" + headers +
                ", body=" + body +
                '}';
    }

    ResponseBody getBody() {
        return body;
    }

    public void setBody(String string) {
        if (string == null)
            this.body = ResponseBody.empty();
        else
            this.body = ResponseBody.of(string);
        setContentHeaders();
    }

    public void setBody(File file) {
        if (file == null)
            this.body = ResponseBody.empty();
        else
            this.body = ResponseBody.of(file);
        setContentHeaders();
    }

    private void setContentHeaders() {
        body.getContentLength()
                .ifPresent(contentLength -> headers.setHeader(HttpHeader.CONTENT_LENGTH.value, contentLength.toString()));
        body.getContentType()
                .ifPresent(contentType -> headers.setHeader(HttpHeader.CONTENT_TYPE.value, contentType.mimeType));

    }
}
