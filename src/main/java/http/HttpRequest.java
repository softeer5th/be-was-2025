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

    public void setMethod(String methodName){
        HttpMethod httpMethod;
        // 모든 http 메소드를 등록한 것이 아니기 때문에 존재하지 않은 메서드가 들어왔을 때 GET으로 처리한다.
        if((httpMethod = HttpMethod.getHttpMethod(methodName)) == null) {
            this.method = HttpMethod.GET;
            return;
        }
        this.method = httpMethod;
    }

    public void setPath(String path){
        this.path = path;
    }

    public void setProtocol(String protocol){
        this.protocol = protocol;
    }

    public void putQueryParam(String key, String value){
        queryParams.put(key, value);
    }
}
