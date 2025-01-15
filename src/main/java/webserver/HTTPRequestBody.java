package webserver;

import java.util.Map;

public class HTTPRequestBody {
    private byte[] body;

    public HTTPRequestBody(byte[] body) {
        this.body = body;
    }

    public String getBodyToString() {
        return new String(body);
    }
}
