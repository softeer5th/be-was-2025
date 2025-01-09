package http;

import java.util.HashMap;
import java.util.Map;

public enum HttpMethod {
    GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE");

    String methodName;
    private static final Map<String, HttpMethod> httpMethods = new HashMap<>();

    HttpMethod(String methodName) {
        this.methodName = methodName;
    }

    static{
        for(HttpMethod httpMethod: HttpMethod.values()){
            httpMethods.put(httpMethod.methodName, httpMethod);
        }
    }

    public static HttpMethod getHttpMethod(String methodName){
        return httpMethods.get(methodName);
    }
}
