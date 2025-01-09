package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequest.class);

    private String method;
    private String target;
    private String version;
    private final Map<String,String> queries = new HashMap<>();
    private List<String> headers;


    public HttpRequest(String method, String target, String version, List<String> headers) {
        this.method = method;
        this.version = version;
        this.headers = headers;

        String[] requestTarget = resolveRequestTarget(target);
        this.target = requestTarget[0];
        if (requestTarget.length > 1) {
            String[] queryArray = resolveQuery(requestTarget[1]);
            for (String s : queryArray) {
                String[] items = s.split("=");
                String key = items[0];
                String value = items.length > 1 ? items[1] : null;
                queries.put(key, value);
            }
        }
    }

    private String[] resolveRequestTarget(String target) {
        return target.split("\\?");
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

    public List<String> getHeaders() {
        return headers;
    }
}
