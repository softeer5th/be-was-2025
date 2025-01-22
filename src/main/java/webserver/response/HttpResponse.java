package webserver.response;

import webserver.enums.ContentType;
import webserver.enums.HttpHeader;
import webserver.enums.HttpStatusCode;
import webserver.header.ResponseHeader;
import webserver.header.SetCookie;
import webserver.response.body.ResponseBody;
import webserver.view.ModelAndTemplate;

import java.io.File;

// HTTP 응답에 대한 정보를 담는 객체
public class HttpResponse {
    private final HttpStatusCode statusCode;
    private final ResponseHeader headers;
    private ResponseBody body;

    private ModelAndTemplate modelAndTemplate;

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

    public static HttpResponse render(String templateName) {
        HttpResponse response = new HttpResponse(HttpStatusCode.OK);
        response.renderTemplate(new ModelAndTemplate(templateName));
        return response;
    }

    public void renderTemplate(ModelAndTemplate modelAndTemplate) {
        if (!this.body.equals(ResponseBody.empty())) {
            throw new IllegalArgumentException("이미 Body가 설정되어 있습니다.");
        }
        this.modelAndTemplate = modelAndTemplate;
    }

    public ModelAndTemplate getModelAndTemplate() {
        return modelAndTemplate;
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

    // Set-Cookie 헤더를 추가하는 메서드
    public void setCookie(SetCookie setCookie) {
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

    public void setBody(byte[] body, ContentType contentType) {
        if (body == null)
            this.body = ResponseBody.empty();
        else
            this.body = ResponseBody.of(body, contentType);
        setContentHeaders();
    }

    ResponseBody getBody() {
        return body;
    }

    public void setBody(String string) {
        if (string == null)
            this.body = ResponseBody.empty();
        else
            this.body = ResponseBody.of(string.getBytes(), ContentType.TEXT_PLAIN);
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
