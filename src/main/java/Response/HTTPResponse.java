package Response;

import constant.HTTPCode;

import java.util.HashMap;
import java.util.Map;

public class HTTPResponse {
    private String httpVersion;
    private HTTPCode httpCode;
    private Map<String, String> headers = new HashMap<>();
    private Object body;

    private HTTPResponse(){}

    public String getHttpVersion() {
        return httpVersion;
    }
    public HTTPCode getHttpCode() {
        return httpCode;
    }
    public Map<String, String> getHeaders() {
        return headers;
    }
    public Object getBody() {
        return body;
    }


    public static HTTPResponse createSuccessResponse(String httpVersion, HTTPCode httpCode, String body){
        HTTPResponse httpResponse = new HTTPResponse();
        httpResponse.httpVersion = httpVersion;
        httpResponse.httpCode = httpCode;
        setSuccessHeaders(httpResponse,httpCode,body);
        httpResponse.body = body;
        return httpResponse;
    }

    public static HTTPResponse createResourceResponse(String httpVersion, HTTPCode httpCode, String resourceName, byte[] content){
        HTTPResponse httpResponse = new HTTPResponse();
        httpResponse.httpVersion = httpVersion;
        httpResponse.httpCode = httpCode;
        setResourceHeaders(httpResponse, content, resourceName);
        httpResponse.body = content;
        return httpResponse;
    }

    public static HTTPResponse createRedirectResponse(String httpVersion, HTTPCode httpCode, String location){
        HTTPResponse httpResponse = new HTTPResponse();
        httpResponse.httpVersion = httpVersion;
        httpResponse.httpCode = httpCode;
        setRedirectHeaders(httpResponse, location);
        return httpResponse;
    }

    public static HTTPResponse createLoginRedirectResponse(String httpVersion, HTTPCode httpCode, String location, String sessionId){
        HTTPResponse httpResponse = new HTTPResponse();
        httpResponse.httpVersion = httpVersion;
        httpResponse.httpCode = httpCode;
        setLoginRedirectHeaders(httpResponse, location, sessionId);
        return httpResponse;
    }


    public static HTTPResponse createFailResponse(String httpVersion, HTTPCode httpCode){
        HTTPResponse httpResponse = new HTTPResponse();
        httpResponse.httpVersion = httpVersion;
        httpResponse.httpCode = httpCode;
        setFailHeaders(httpResponse,httpCode);
        httpResponse.body = httpCode.getResponseBody();
        return httpResponse;
    }

    private static void setFailHeaders(HTTPResponse httpResponse, HTTPCode httpCode) {
        httpResponse.headers.put("Content-Type", "text/html; charset=utf-8");
        httpResponse.headers.put("Content-Length", String.valueOf(httpCode.getResponseBody().length()));
    }

    private static void setResourceHeaders(HTTPResponse httpResponse, byte[] content, String resourceName){
        String contentType = getContentType(resourceName);
        httpResponse.headers.put("Content-Type", contentType);
        httpResponse.headers.put("Content-Length", String.valueOf(content.length));
    }

    private static void setRedirectHeaders(HTTPResponse httpResponse, String location) {
        httpResponse.headers.put("Location", location);
    }

    private static void setLoginRedirectHeaders(HTTPResponse httpResponse, String location, String sessionId) {
        httpResponse.headers.put("Location", location);
        httpResponse.headers.put("Set-Cookie", sessionId + "; Path=/");
    }

    private static void setSuccessHeaders(HTTPResponse httpResponse, HTTPCode httpCode, String body){
        httpResponse.headers.put("Content-Type", "application/octet-stream");
        httpResponse.headers.put("Content-Length", String.valueOf(body.length()));
    }
    private static String getContentType(String resourceName) {
        String extension = resourceName.substring(resourceName.lastIndexOf(".") + 1);

        return switch (extension) {
            case "html" -> "text/html";
            case "css" -> "text/css";
            case "js" -> "text/javascript";
            case "ico" -> "image/x-icon";
            case "png" -> "image/png";
            case "jpg" -> "image/jpeg";
            case "svg" -> "image/svg+xml";
            default -> "application/octet-stream";
        };
    }

}
