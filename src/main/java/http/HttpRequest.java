package http;

import http.constant.HttpHeader;
import http.constant.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.Cookie;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.Base64;
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
    private final byte[] body;

    private final URI uri;

    private final Map<String, String> sessionIds;


    public HttpRequest(String method, String path, String version, List<String> request) throws UnsupportedEncodingException {
        this.method = HttpMethod.valueOf(method.toUpperCase());
        this.version = version;

        this.uri = URI.create(path);
        this.path = URLDecoder.decode(uri.getPath(), "UTF-8");

        this.headers = parseHeaders(request);
        this.queries = parseQuery(uri.getQuery());
        this.body = extractBody(request);

        this.sessionIds = extractSessionIds();
    }

    private Map<String, String> extractSessionIds() {
        Map<String, String> ids = new HashMap<>();
        if (!headers.containsKey(HttpHeader.COOKIE.value().toLowerCase())) {
            return ids;
        }

        return Cookie.parse(headers.get(HttpHeader.COOKIE.value().toLowerCase()));
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
        Map<String, String> query = new HashMap<>();
        if (queryString == null) {
            return query;
        }

        String[] queryArray = resolveQuery(queryString);
        for (String s : queryArray) {
            String[] items = s.split("=");
            String key = items[0].trim();
            String value = items.length > 1 ? items[1].trim() : null;
            query.put(key, value);
        }
        return query;
    }

    private byte[] extractBody(List<String> request) {
        if (!headers.containsKey(HttpHeader.CONTENT_LENGTH.value().toLowerCase())) {
            return null;
        }
        int len = request.size();
        byte[] result = request.get(len - 1).getBytes();
        logger.debug("request Body: {}", new String(result));
        return result;
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

    public byte[] getBody() {
        return body;
    }

    public Map<String, String> getSessionIds() {
        return sessionIds;
    }
}
