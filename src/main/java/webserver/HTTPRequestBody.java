package webserver;

public class HTTPRequestBody {
    private final byte[] body;

    public HTTPRequestBody(byte[] body) {
        this.body = body;
    }

    public String getBodyToString() {
        if (body == null || body.length == 0) {
            throw new HTTPExceptions.Error400("Body is null");
        }
        return new String(body);
    }
}
