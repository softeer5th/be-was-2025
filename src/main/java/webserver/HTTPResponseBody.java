package webserver;

public class HTTPResponseBody {
    private final byte[] body;

    public HTTPResponseBody(byte[] body) {
        this.body = body;
    }

    public byte[] getBody() {
        return body;
    }

    public int getBodyLength() {
        return body.length;
    }
}
