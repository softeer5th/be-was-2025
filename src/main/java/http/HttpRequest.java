package http;

import http.enums.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private HttpMethod method;
    private String path;
    private String protocol;
    private final Map<String, String> queryParams = new HashMap<>();
    private final Map<String, String> headers = new HashMap<>();
    private char[] body;

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

    public String getHeader(String headerKey){
        return headers.get(headerKey);
    }

    public char[] getBody(){
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
        headers.put(headerKey, headerValue);
    }

    public void setBody(char[] body){
        this.body = body;
    }
}
