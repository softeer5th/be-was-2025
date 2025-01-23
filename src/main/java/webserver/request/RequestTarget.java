package webserver.request;

import java.util.Map;

/**
 * Http Reqeust의 Request-Target(URL)을 나타내는 클래스
 */
public class RequestTarget {
    private final String path;
    private final Map<String, String> query;

    /**
     * RequestTarget 생성자
     *
     * @param path  path
     * @param query query
     */
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
