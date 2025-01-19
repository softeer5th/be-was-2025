package webserver.router;

import webserver.enums.HttpMethod;
import webserver.handler.HttpHandler;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

// Path 기반 라우팅을 위한 클래스
public class PathRouter {
    private static final String ALL_PATH = "*";
    private static final String SEGMENT_DELIMITER = "/";
    private static final String PATH_VARIABLE_PREFIX = "{";
    private static final String PATH_VARIABLE_SUFFIX = "}";
    private final Map<HttpMethod, RouterTrieNode> rootMap;
    private HttpHandler defaultHandler;

    public PathRouter() {
        this.rootMap = new EnumMap<>(HttpMethod.class);
        for (HttpMethod method : HttpMethod.values()) {
            rootMap.put(method, new RouterTrieNode());
        }
    }

    public PathRouter setDefaultHandler(HttpHandler handler) {
        defaultHandler = handler;
        return this;
    }

    public PathRouter setHandler(String path, HttpHandler handler) {
        for (HttpMethod method : HttpMethod.values()) {
            setHandler(method, path, handler);
        }
        return this;
    }

    public PathRouter setHandler(HttpMethod method, String path, HttpHandler handler) {
        if ("/".equals(path)) {
            rootMap.get(method).handler = handler;
        }

        String[] segments = pathToSegments(path);

        RouterTrieNode current = rootMap.get(method);
        for (String segment : segments) {
            if (isPathVariable(segment)) {
                // segment가 path variable인 경우
                current.setPathVariableMetadata(
                        extractPathVariableName(segment)
                );
                // 동적 경로이므로 모든 segment를 처리할 수 있도록 ALL_PATH로 설정
                current = current.getOrCreateChild(ALL_PATH);
            } else {
                // segment가 path variable이 아닌 경우
                current = current.getOrCreateChild(segment);
            }
        }

        current.setHandler(handler);

        return this;
    }


    public RoutingResult route(HttpMethod method, String path) {
        if ("/".equals(path)) {
            return new RoutingResult(rootMap.get(method).handler, Map.of());
        }

        String[] segments = pathToSegments(path);

        // 탐색 과정에서 path variable의 값을 저장하기 위한 map
        // Map<path variable name, path variable value> 형태로 저장
        Map<String, String> pathVariables = new HashMap<>();

        RouterTrieNode current = this.rootMap.get(method);
        for (String segment : segments) {
            // 정적인 경로로 등록된 Handler를 우선순위가 높게 처리
            if (current.children.containsKey(segment)) {
                current = current.children.get(segment);
            } else if (current.children.containsKey(ALL_PATH)) {
                pathVariables.put(current.pathVariableName, segment);
                current = current.children.get(ALL_PATH);
            } else {
                return new RoutingResult(defaultHandler, Map.of());
            }

        }

        // 알맞은 핸들러가 없는 경우 defaultHandler를 반환
        if (current.handler == null) {
            return new RoutingResult(defaultHandler, Map.of());
        }
        return new RoutingResult(current.handler, pathVariables);

    }

    private String[] pathToSegments(String path) {
        // path가 "/"로 시작하는 경우 제거 -> path.split("/")[0] 이 빈 문자열이 되는 것을 방지
        return removeLeadingSlash(path)
                .split(SEGMENT_DELIMITER);
    }

    // segment가 path variable인지 확인
    private boolean isPathVariable(String segment) {
        return segment.startsWith(PATH_VARIABLE_PREFIX) &&
                segment.endsWith(PATH_VARIABLE_SUFFIX);
    }

    // path variable의 이름을 추출. ex) {id} -> id
    private String extractPathVariableName(String segment) {
        if (!isPathVariable(segment)) {
            throw new IllegalArgumentException("This path segment is not a path variable");
        }
        return segment.substring(1, segment.length() - 1);
    }

    // path가 "/"로 시작하는 경우 제거 ex) "/a/b" -> "a/b"
    private String removeLeadingSlash(String path) {
        if (path.startsWith(SEGMENT_DELIMITER)) {
            return path.substring(1);
        }
        return path;
    }

    // Trie의 Node
    private static class RouterTrieNode {
        private final Map<String, RouterTrieNode> children = new HashMap<>();

        private HttpHandler handler;

        private String pathVariableName;

        RouterTrieNode getOrCreateChild(String segment) {
            return children.computeIfAbsent(segment, key -> new RouterTrieNode());
        }

        void setPathVariableMetadata(String pathVariableName) {
            if (this.pathVariableName != null && !this.pathVariableName.equals(pathVariableName)) {
                throw new IllegalArgumentException("서로 다른 이름의 path variable은 지원하지 않습니다.");
            }
            this.pathVariableName = pathVariableName;
        }

        void setHandler(HttpHandler handler) {
            this.handler = handler;
        }
    }

    // 매칭된 핸들러와 path variable을 저장하는 record
    public record RoutingResult(HttpHandler handler, Map<String, String> pathVariables) {
    }
}
