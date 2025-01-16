package webserver;

import java.util.HashMap;
import java.util.Map;

public class HTTPResponseHeader {
    private final String version;
    private int statusCode;
    Map<String, String> headers;

    private static final Map<Integer, String> STATUS_CODE_MAP = Map.of(
            200, "200 OK",
            302, "302 Found",
            400, "400 Bad Request",
            404, "404 Not Found",
            405, "405 Method Not Allowed",
            409, "409 Conflict",
            415, "415 Unsupported Media Type",
            500, "500 Internal Server Error"
    );

    public HTTPResponseHeader(String version) {
        this.version = version;
        headers = new HashMap<String, String>();
    }

    public String getVersion() {
        return version;
    }

    public String getStatusCode() {
        if (STATUS_CODE_MAP.containsKey(statusCode)) {
            return STATUS_CODE_MAP.get(statusCode);
        }

        throw new HTTPExceptions.Error500("500 Internal Server Error");
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }
}
