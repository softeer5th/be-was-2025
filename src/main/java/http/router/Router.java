package http.router;

import http.enums.ContentType;
import http.enums.HttpMethod;
import http.handler.BadRequestHandler;
import http.handler.Handler;
import http.handler.StaticResourceHandler;
import http.handler.UserHandler;
import http.request.HttpRequest;
import http.request.TargetInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private Map<String, Handler> routes = new HashMap<>();
    private final Handler staticResourceHandler = StaticResourceHandler.getInstance();
    private final Handler userHandler = UserHandler.getInstance();
    private final Handler badRequestHandler = BadRequestHandler.getInstance();
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
        routes.put("/",                 staticResourceHandler);
        routes.put("/article",          staticResourceHandler);
        routes.put("/comment",          staticResourceHandler);
        routes.put("/img",              staticResourceHandler);
        routes.put("/login",            staticResourceHandler);
        routes.put("/main",             staticResourceHandler);
        routes.put("/mypage",           staticResourceHandler);
        routes.put("/registration",     staticResourceHandler);
        routes.put("/user",             userHandler);
        routes.put("/user/create",      userHandler);
    }

    public void addRoute(String path, Handler handler) {
        routes.put(path, handler);
    }

    public Handler route(HttpRequest request) {
        if (request.isInvalid()) return badRequestHandler;

        HttpMethod httpMethod = request.getMethod();
        TargetInfo targetInfo = request.getTarget();
        String path = targetInfo != null ? targetInfo.getPath() : null;
        if (path != null && httpMethod.equals(HttpMethod.GET)) {
            if (path.matches(compareExtensionRegex)) {
                return StaticResourceHandler.getInstance(); // 파일 요청 처리
            }
            for (String target : routes.keySet()) {
                if (path.startsWith(target)) {
                    return routes.get(target);
                }
            }
        }
        return badRequestHandler;
    }
}
