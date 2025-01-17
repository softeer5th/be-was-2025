package webserver.http;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String method;
    private String path;
    private final Map<String, String> params = new HashMap<>();
    private String version;
    private final Map<String, String> headers = new HashMap<>();
    private byte[] body;

    public byte[] getBody() { return body; }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public String getHeader(String name) {
        return headers.get(name.toLowerCase());
    }

    public String getParameter(String name) {return params.get(name);}

    public void setMethod(String method) { this.method = method; }

    public void setPath(String path) { this.path = path; }

    public void setVersion(String version) { this.version = version; }

    public void setHeader(String name, String value) { this.headers.put(name.toLowerCase(), value.toLowerCase()); }

    public void setParameter(String name, String value) { this.params.put(name, value); }

    public void setBody(byte[] body) { this.body = body; }
}

