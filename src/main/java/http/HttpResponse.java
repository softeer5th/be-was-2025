package http;

import http.enums.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private HttpStatus httpStatus;
    private Map<String, String> headers = new HashMap<>();
    private byte[] body;

    public HttpResponse(HttpStatus httpStatus, String contentType, byte[] data){
        this.httpStatus = httpStatus;
        setContentType(contentType);
        setContentLength(data.length);
        this.body = data;
    }

    public HttpStatus getHttpStatus(){
        return this.httpStatus;
    }

    public Map<String, String> getHeaders(){
        return headers;
    }

    public byte[] getBody(){
        return this.body;
    }

    public void setContentType(String contentType){
        headers.put("Content-Type", contentType);
    }

    public void setContentLength(int contentLength){
        headers.put("Content-Length", String.valueOf(contentLength));
    }

}
