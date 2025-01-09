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
    private String target;
    private String version;
    private final Map<String,String> queries = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();

    private final URI uri;


    public HttpRequest(String method, String target, String version, List<String> headers) {
        this.method = method;
        this.version = version;

        for (String header: headers) {
            String[] tokens = header.split(": ");
            this.headers.put(tokens[0], tokens[1]);
        }

        this.uri = URI.create(target);
        this.target = uri.getPath();

        if (uri.getQuery() != null) {
            String[] queryArray = resolveQuery(uri.getQuery());
            for (String s : queryArray) {
                String[] items = s.split("=");
                String key = items[0];
                String value = items.length > 1 ? items[1] : null;
                queries.put(key, value);
            }
        }
    }

    private String[] resolveQuery(String query) {
        return query.split("&");
    }

    public String getMethod() {
        return method;
    }

    public String getTarget() {
        return target;
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
}
