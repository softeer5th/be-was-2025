package webserver.response;

public class HTTPResponse {
    private HTTPResponseHeader responseHeader;
    private HTTPResponseBody responseBody;

    public HTTPResponse(HTTPResponseHeader responseHeader, HTTPResponseBody responseBody) {
        this.responseHeader = responseHeader;
        this.responseBody = responseBody;
    }

    public HTTPResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public HTTPResponseBody getResponseBody() {
        return responseBody;
    }

    public void addCookie(String cookieString) {
        this.responseHeader.addHeader("Set-Cookie", cookieString);
    }
}
