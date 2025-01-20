package webserver.response;

import util.FileFinder;
import util.enums.HttpStatusCode;
import webserver.request.Request;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private HttpStatusCode statusCode;
    private final Map<String, String> headers = new HashMap<>();
    private final Request request;
    private byte[] body = null;

    public Response(Request request) {
        this.request = request;
    }

    public Response(Request request, HttpStatusCode statusCode) {
        this.request = request;
        this.statusCode = statusCode;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode;
    }

    public void setBody(){
        headers.put("Content-Type", request.contentType);
        try {
            FileFinder fileFinder = new FileFinder(request.url);
            if (fileFinder.find()) {
                body = fileFinder.readFileToBytes();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        if(body != null){
            headers.put("Content-Length", String.valueOf(body.length));
        }
        else headers.put("Content-Length", "0");
    }


    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public boolean hasBody(){
        return body != null;
    }

    public byte[] getBody(){
        return body;
    }
}
