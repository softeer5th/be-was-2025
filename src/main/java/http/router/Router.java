package http.router;

import http.enums.ContentType;
import http.enums.HttpMethod;
import http.handler.*;
import http.request.HttpRequest;
import http.request.TargetInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private static final String STATIC_RESOURCE_PATH = "./src/main/resources/static";
    private final Map<String, Handler> routes = new HashMap<>();
    private final Handler staticResourceHandler = StaticResourceHandler.getInstance(STATIC_RESOURCE_PATH);
    private final Handler userHandler = UserHandler.getInstance();
    private final Handler badRequestHandler = BadRequestHandler.getInstance();
    private final Handler postHandler = PostHandler.getInstance();
    private static final String compareExtensionRegex;

    private static final Logger logger = LoggerFactory.getLogger(Router.class);

    public Router() {
        initializeRoutes();
    }

    static {
        StringBuilder sb = new StringBuilder("^/[^/]+\\.(");
        for (ContentType type : ContentType.values()) {
            sb.append(type.getExtension()+"|");
        }
        sb.deleteCharAt(sb.length()-1);
        sb.append(")$");
        compareExtensionRegex = sb.toString();
    }

    private void initializeRoutes() {
        routes.put("/user", userHandler);
        routes.put("/user/create", userHandler);
        routes.put("/user/login", userHandler);
        routes.put("/user/logout", userHandler);
        routes.put("/post/article", postHandler);
        routes.put("/post/comment", postHandler);
    }

    public void addRoute(String path, Handler handler) {
        routes.put(path, handler);
    }

    public Handler route(HttpRequest request) {
        if (request.isInvalid()) return badRequestHandler;

        HttpMethod httpMethod = request.getMethod();
        TargetInfo targetInfo = request.getTarget();
        String path = targetInfo != null ? targetInfo.getPath() : null;

        if (path != null) {
            if (!path.matches(compareExtensionRegex)) { // 확장자를 가지지 않은 파일 및 경로에 대한 요청인 경우 동적 요청에 대해 우선 순위로 처리
                for (String target : routes.keySet()) {
                    if (path.startsWith(target)) {
                        return routes.get(target);
                    }
                }
            }
            return staticResourceHandler; // 정적 파일 요청 처리
        }
        return badRequestHandler;
    }
}
