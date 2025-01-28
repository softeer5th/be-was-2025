package http.request;

import java.util.HashMap;
import java.util.Map;

public class MultipartPart {
    private final Map<String, String> headers = new HashMap<>();
    private byte[] body;

    public MultipartPart() {
        // 기본 생성자
    }
    public void addHeader(String name, String value){
        headers.put(name, value);
    }
    
    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }
}