package router;

import exception.BaseException;
import exception.HttpErrorCode;
import handler.Handler;
import handler.StaticFileHandler;
import handler.UserLoginHandler;
import handler.UserRegisterHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestRouter implements Router {

    private static final Logger logger = LoggerFactory.getLogger(RequestRouter.class);

    private final Map<Pattern, Handler> routeMap;

    public RequestRouter() {
        routeMap = new HashMap<>();
        routeMap.put(Pattern.compile("^/users/register$"), new UserRegisterHandler());
        routeMap.put(Pattern.compile("^/users/login$"), new UserLoginHandler());
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

        logger.error("No handler found for path {}", path);
        throw new BaseException(HttpErrorCode.NOT_FOUND_PATH);
    }
}