package router;

import config.AppConfig;
import exception.BaseException;
import exception.HttpErrorCode;
import handler.*;

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
        routeMap.put(Pattern.compile("^/users/register$"), AppConfig.getUserRegisterHandler());
        routeMap.put(Pattern.compile("^/users/login$"), AppConfig.getUserLoginHandler());
        routeMap.put(Pattern.compile("^/users/logout$"), AppConfig.getUserLogoutHandler());
        routeMap.put(Pattern.compile("^.*\\.(html|css|js|svg|ico|jpg|png)$"),
                AppConfig.getFileRequestHandler());
        routeMap.put(Pattern.compile("^/[^/]+$"), AppConfig.getFileRequestHandler());
        routeMap.put(Pattern.compile("^/$"), AppConfig.getFileRequestHandler());
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