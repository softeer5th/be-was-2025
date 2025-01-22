package webserver.response;

import webserver.enums.ContentType;
import webserver.enums.HttpHeader;
import webserver.enums.HttpStatusCode;
import webserver.header.ResponseHeader;
import webserver.header.SetCookie;
import webserver.response.body.ResponseBody;
import webserver.view.ModelAndTemplate;

import java.io.File;

/**
 * HTTP 응답에 대한 정보를 담는 객체
 */
public class HttpResponse {
    private final HttpStatusCode statusCode;
    private final ResponseHeader headers;
    private ResponseBody body;

    private ModelAndTemplate modelAndTemplate;

    /**
     * HttpResponse 생성자
     *
     * @param statusCode 응답으로 보낼 코드
     */
    public HttpResponse(HttpStatusCode statusCode) {
        this(statusCode, new ResponseHeader());
    }

    /**
     * HttpResponse 생성자
     *
     * @param statusCode 응답으로 보낼 코드
     * @param headers    응답으로 보낼 헤더
     */
    public HttpResponse(HttpStatusCode statusCode, ResponseHeader headers) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = ResponseBody.empty();
    }

    /**
     * 302 Found 상태 코드와 Location 헤더를 가진 HttpResponse를 반환하는 메서드
     *
     * @param location 리다이렉트할 URL
     * @return HttpResponse
     */
    public static HttpResponse redirect(String location) {
        return new HttpResponse(HttpStatusCode.FOUND)
                .setHeader(HttpHeader.LOCATION.value, location);
    }

    /**
     * 템플릿 파일을 렌더링하는 HttpResponse를 반환하는 팩토리 메서드
     *
     * @param templateName 렌더링할 템플릿 파일 이름
     * @return HttpResponse
     */
    public static HttpResponse render(String templateName) {
        HttpResponse response = new HttpResponse(HttpStatusCode.OK);
        response.renderTemplate(new ModelAndTemplate(templateName));
        return response;
    }

    /**
     * 템플릿 파일을 렌더링하는 HttpResponse를 반환하는 메서드
     *
     * @param modelAndTemplate 렌더링할 템플릿과 데이터
     * @throws IllegalArgumentException 이미 Body가 설정되어 있을 때
     */
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

    /**
     * 헤더를 추가하는 메서드
     * 헤더 이름이 이미 존재할 경우 덮어씀
     *
     * @param key   헤더 이름
     * @param value 헤더 값
     * @return this
     */
    public HttpResponse setHeader(String key, String value) {
        headers.setHeader(key, value);
        return this;
    }

    /**
     * Set-Cookie 헤더를 추가하는 메서드
     *
     * @param setCookie 추가할 SetCookie 객체
     */
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

    /**
     * body를 설정하는 메서드
     *
     * @param body        body로 사용할 byte 배열
     * @param contentType body의 content type
     */
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

    /**
     * body를 설정하는 메서드
     *
     * @param string body로 사용할 문자열
     */
    public void setBody(String string) {
        if (string == null)
            this.body = ResponseBody.empty();
        else
            this.body = ResponseBody.of(string.getBytes(), ContentType.TEXT_PLAIN);
        setContentHeaders();
    }

    /**
     * body를 설정하는 메서드
     *
     * @param file body로 사용할 파일
     */
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
