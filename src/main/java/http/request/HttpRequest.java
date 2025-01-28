package http.request;

import http.cookie.Cookie;
import http.enums.HttpMethod;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private HttpMethod method;
    private String path;
    private String protocol;
    private String boundary;
    private final Map<String, String> queryParams = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();
    private final Map<String, Cookie> cookies = new HashMap<>();

    private final Map<String, MultipartPart> multipartParts = new HashMap<>();
    private byte[] body;

    public HttpRequest(){}

    public HttpMethod getMethod(){
        return this.method;
    }

    public String getPath(){
        return this.path;
    }

    public String getProtocol(){
        return this.protocol;
    }

    public String getQueryParam(String key){
        return queryParams.get(key);
    }

    public Map<String, String> getQueryParams(){return queryParams;}

    public String getHeader(String headerKey){
        return headers.get(headerKey);
    }

    public byte[] getBody(){
        return this.body;
    }
    public void setMethod(String methodName){
        this.method =  HttpMethod.valueOf(methodName.toUpperCase());
    }

    public void setPath(String path){
        this.path = path;
    }

    public void setProtocol(String protocol){
        this.protocol = protocol.toUpperCase();
    }

    public void addQueryParam(String key, String value){
        queryParams.put(key, value);
    }

    public void addHeader(String headerKey, String headerValue){
        headers.put(headerKey.toLowerCase(), headerValue);
    }

    public void setBody(byte[] body){
        this.body = body;
    }

    public Cookie getCookie(String cookieName){
        return cookies.get(cookieName.toLowerCase());
    }

    public void addCookie(Cookie cookie){
        cookies.put(cookie.getName().toLowerCase(), cookie);
    }

    public void setBoundary(String boundary){
        this.boundary = boundary;
    }

    public String getBoundary(){
        return this.boundary;
    }

    public void addMultipartPart(String name, MultipartPart part){
        this.multipartParts.put(name, part);
    }

    public MultipartPart getMultipartPart(String name){
        return this.multipartParts.get(name);
    }

    public Map<String, String> convertBodyToMap(){
        Map<String, String> dataMap = new HashMap<>();

        String bodyString = URLDecoder.decode(new String(body, StandardCharsets.UTF_8), StandardCharsets.UTF_8);

        String[] bodyParts = bodyString.split("&");

        for(String bodyPart: bodyParts){
            String[] keyValue = bodyPart.trim().split("=");

            dataMap.put(keyValue[0], keyValue[1]);
        }

        return dataMap;
    }
}
