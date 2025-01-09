package webserver.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// request path와 http handler를 매핑하는 클래스
public class HttpHandlerMapping {
    private final Map<String, HttpHandler> handlerMap;
    private HttpHandler defaultHandler;

    public HttpHandlerMapping() {
        this.handlerMap = new HashMap<>();
    }

    public HttpHandlerMapping setDefaultHandler(HttpHandler handler) {
        this.defaultHandler = handler;
        return this;
    }

    // path와 handler를 매핑
    public HttpHandlerMapping setHandler(String path, HttpHandler handler) {
        path = extractPath(path);
        handlerMap.put(path, handler);
        return this;
    }

    /* path에 해당하는 handler 반환
       핸들러 탐색 예제)
       handlerMap = {
        "/a/b": ABHandler,
        "/a" : AHandler
       }
       일 때 path = "/a/b/c" 가 들어오면 이를 이용홰
       candidatePaths = ["/a/b/c", "/a/b", "/a"] 를 만든다.
       해당 순서대로 handlerMap을 탐색하여 ABHandler를 반환한다.
     */
    public HttpHandler getHandler(String path) {
        List<String> candidatePaths = getCandidatePaths(path);
        HttpHandler handler = null;
        for (String candidatePath : candidatePaths) {
            handler = this.handlerMap.get(candidatePath);
            if (handler != null) {
                return handler;
            }
        }
        return defaultHandler;
    }


    // path에서 query string을 제거하고 반환
    private String extractPath(String path) {
        if (path.contains("?")) {
            return path.substring(0, path.indexOf("?"));
        }
        return path;
    }

    // path를 마지막 세그먼트를 제거하며 순서대로 반환 ex) '/a/b' -> ['/a/b/c', '/a/b', '/a']
    // 이 순서대로 path를 찾아보며 handler를 찾는다. 따라서 긴 path의 핸들러가 우선순위가 높다.
    private List<String> getCandidatePaths(String path) {
        path = extractPath(path);
        List<String> candidatePaths = new ArrayList<>();
        while (path != null && !path.isBlank()) {
            candidatePaths.add(path);
            path = removeLastPathSegment(path);
        }
        return candidatePaths;
    }

    // path의 마지막 segment를 제거하고 반환. ex) '/a/b/c' -> '/a/b'
    // 더 이상 segment가 없다면 null을 반환 ex) '/' -> null
    private String removeLastPathSegment(String path) {
        path = removeTrailSlash(path);
        int lastSlashIndex = path.lastIndexOf("/");
        if (lastSlashIndex == -1)
            return null;
        return path.substring(0, lastSlashIndex);
    }

    // path가 /로 끝나면 /를 제거하고 반환 ex) '/a/b/' -> '/a/b'
    private String removeTrailSlash(String path) {
        if (path.endsWith("/")) {
            return path.substring(0, path.length() - 1);
        }
        return path;
    }
}
