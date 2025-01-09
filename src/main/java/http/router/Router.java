package http.router;

import http.request.HttpRequest;
import http.request.TargetInfo;
import http.enums.HttpMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import http.response.ContentType;
import http.handler.Handler;
import http.handler.NotFoundHandler;
import http.handler.StaticResourceHandler;
import http.handler.UserHandler;

import java.util.HashMap;
import java.util.Map;

public class Router {
    private Map<String, Handler> routes = new HashMap<>();
    private Handler notFoundHandler = NotFoundHandler.getInstance();
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
        System.out.println(compareExtensionRegex);
    }

    private void initializeRoutes() {
        routes.put("/",                 StaticResourceHandler.getInstance());
        routes.put("/article",          StaticResourceHandler.getInstance());
        routes.put("/comment",          StaticResourceHandler.getInstance());
        routes.put("/img",              StaticResourceHandler.getInstance());
        routes.put("/login",            StaticResourceHandler.getInstance());
        routes.put("/main",             StaticResourceHandler.getInstance());
        routes.put("/mypage",           StaticResourceHandler.getInstance());
        routes.put("/registration",     StaticResourceHandler.getInstance());
        routes.put("/user",             UserHandler.getInstance());
        routes.put("/user/create",      UserHandler.getInstance());
        routes.put("INVALID REQUEST",   NotFoundHandler.getInstance());
    }

    public void addRoute(String path, Handler handler) {
        routes.put(path, handler);
    }

    public Handler route(HttpRequest request) {
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
        return notFoundHandler;
    }
}
