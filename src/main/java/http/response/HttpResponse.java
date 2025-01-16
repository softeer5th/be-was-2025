package http.response;

import http.enums.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private HttpStatus httpStatus;
    private Map<String, String> headers = new HashMap<>();
    private byte[] body;

    public HttpResponse(HttpStatus httpStatus, String contentType, int contentLength, String location, byte[] data){
        this.httpStatus = httpStatus;
        setContentType(contentType);
        setContentLength(contentLength);
        setLocation(location);
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

    public void setLocation(String location){
        headers.put("Location", location);
    }

    public void setBody(byte[] body){
        this.body = body;
    }
    public static class Builder{
        private HttpStatus httpStatus;
        private String contentType;
        private int contentLength;
        private String location;
        private byte[] body;


        public Builder(){
        }

        public Builder httpStatus(HttpStatus httpStatus){
            this.httpStatus = httpStatus;
            return this;
        }

        public Builder contentType(String contentType){
            this.contentType = contentType;
            return this;
        }

        public Builder contentLength(int contentLength){
            this.contentLength = contentLength;
            return this;
        }
        public Builder location(String location){
            this.location = location;
            return this;
        }

        public Builder body(byte[] body){
            this.contentLength = body.length;
            this.body = body;
            return this;
        }

        public HttpResponse build(){
            return new HttpResponse(httpStatus, contentType, contentLength, location, body);
        }
    }
}
