package webserver.request;

import util.enums.Mime;

import java.util.HashMap;
import java.util.Map;

public class Request {
    private final Map<String, String> headers = new HashMap<>();
    private String requestLine;
    private String body = null;
    public String method;
    public String url = "/";
    public String extension = "html";
    public String parameter = null;
    public String contentType = "text/html";

    void setRequestLine(String requestLine) {
        this.requestLine = requestLine;
    }

    void setMethod(String method){
        this.method = method;
    }

    void setUrl(String url){
        this.url = url;
    }

    void setContentType(String url){
        String[] tokens = url.split("\\.");
        if(tokens.length > 1) {
            this.extension = tokens[1];
            this.contentType = Mime.getByExtension(extension).getContentType();
        }
    }

    void setParameter(String parameter){
        this.parameter = parameter;
    }

    void addHeader(String key, String value){
        headers.put(key, value);
    }

    void setBody(String body){
        this.body = body;
    }

    public Map<String,String> getHeaders(){
        return headers;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getContentType() {
        return contentType;
    }

    public String getBody() {
        return body;
    }

    public String getRequestLine(){
        return requestLine;
    }
}
