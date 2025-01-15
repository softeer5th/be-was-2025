package http;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private final HttpMethod method;
    private final String path;
    private final String version;
    private final Map<String,String> queries;
    private final Map<String, String> headers;
    private final String body;

    private final URI uri;


    public HttpRequest(String method, String path, String version, List<String> request) throws UnsupportedEncodingException {
        this.method = HttpMethod.valueOf(method.toUpperCase());
        this.version = version;

        this.uri = URI.create(path);
        this.path = URLDecoder.decode(uri.getPath(), "UTF-8");

        this.headers = parseHeaders(request);
        this.queries = parseQuery(uri.getQuery());
        this.body = extractBody(request);
    }

    private Map<String, String> parseHeaders(List<String> request) {
        Map<String, String> headers = new HashMap<>();
        for (String header: request) {
            if (header.isBlank()) break;
            String[] tokens = header.split(":", 2);
            headers.merge(tokens[0].trim().toLowerCase(), tokens[1].trim(), String::concat);
        }

        return headers;
    }

    private Map<String, String> parseQuery(String queryString) {
        if (queryString == null) {
            return null;
        }
        Map<String, String> query = new HashMap<>();

        String[] queryArray = resolveQuery(queryString);
        for (String s : queryArray) {
            String[] items = s.split("=");
            String key = items[0];
            String value = items.length > 1 ? items[1] : null;
            query.put(key.toLowerCase(), value);
        }
        return query;
    }

    private String extractBody(List<String> request) {
        return headers.computeIfPresent(HttpHeader.CONTENT_LENGTH.value().toLowerCase(), (k, v) -> {
            int len = request.size();
            return request.get(len - 1);
        });
    }

    private String[] resolveQuery(String query) {
        return query.split("&");
    }

    public HttpMethod getMethod() {
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
