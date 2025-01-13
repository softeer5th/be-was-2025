package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private String method;
    private String path;
    private String version;
    private final Map<String,String> queries = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();
    private  String body;

    private final URI uri;


    public HttpRequest(String method, String path, String version, List<String> request) {
        this.method = method;
        this.version = version;

        for (String header: request) {
            if (header.isBlank()) break;
            String[] tokens = header.split(": ");
            this.headers.put(tokens[0].toLowerCase(), tokens[1]);
        }

        this.uri = URI.create(path);
        this.path = uri.getPath();

        if (uri.getQuery() != null) {
            String[] queryArray = resolveQuery(uri.getQuery());
            for (String s : queryArray) {
                String[] items = s.split("=");
                String key = items[0];
                String value = items.length > 1 ? items[1] : null;
                queries.put(key.toLowerCase(), value);
            }
        }

        this.headers.computeIfPresent("content-length", (k, v) -> {
            int len = request.size();
            this.body = request.get(len - 1);

            return v;
        });
    }

    private String[] resolveQuery(String query) {
        return query.split("&");
    }

    public String getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getVersion() {
        return version;
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public URI getUri() {
        return uri;
    }

    public String getBody() {
        return body;
    }
}
