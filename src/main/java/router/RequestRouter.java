package router;

import exception.BaseException;
import exception.HttpErrorCode;
import handler.Handler;
import handler.StaticFileHandler;
import handler.UserRegisterHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RequestRouter implements Router {

    private final Map<Pattern, Handler> routeMap;

    public RequestRouter() {
        routeMap = new HashMap<>();
        routeMap.put(Pattern.compile("^/users/register\\?[^?]+$"), new UserRegisterHandler());
        routeMap.put(Pattern.compile("^.*\\.(html|css|js|svg|ico|jpg|png)$"),
            new StaticFileHandler());
        routeMap.put(Pattern.compile("^/[^/]+$"), new StaticFileHandler());
        routeMap.put(Pattern.compile("^/$"), new StaticFileHandler());
    }

    public Handler route(String path) {
        for (Map.Entry<Pattern, Handler> entry : routeMap.entrySet()) {
            if (entry.getKey().matcher(path).matches()) {
                return entry.getValue();
            }
        }
        throw new BaseException(HttpErrorCode.NOT_FOUND_PATH);
    }
}