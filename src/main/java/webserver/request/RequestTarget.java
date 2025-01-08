package webserver.request;

import java.util.Map;

public class RequestTarget {
    private final String path;
    private final Map<String, String> query;

    public RequestTarget(String path, Map<String, String> query) {
        this.path = path;
        this.query = query;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return "RequestTarget{" +
                "path='" + path + '\'' +
                ", query=" + query +
                '}';
    }
}
