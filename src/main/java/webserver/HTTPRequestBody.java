package webserver;

public class HTTPRequestBody {
    private final byte[] body;

    public HTTPRequestBody(byte[] body) {
        this.body = body;
    }

    public String getBodyToString() {
        return new String(body);
    }
}
