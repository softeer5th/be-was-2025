package webserver.response;

import util.FileFinder;
import util.RequestParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Response {
    private final Map<String, String> headers = new HashMap<>();
    private final RequestParser request;
    private byte[] body;

    public Response(RequestParser request) {
        this.request = request;
        headers.put("Content-Type", request.contentType);
        makeBody();
    }

    public String getHeader(String name) {
        return headers.get(name);
    }

    public void makeBody(){
        byte[] body = null;
        try {
            FileFinder fileFinder = new FileFinder(request.url);
            if (fileFinder.find()) {
                body = fileFinder.readFileToBytes();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        this.body = body;
        if(body != null){
            headers.put("Content-Length", String.valueOf(body.length));
        }
        else headers.put("Content-Length", "0");
    }

    public byte[] getBody(){
        return body;
    }
}
