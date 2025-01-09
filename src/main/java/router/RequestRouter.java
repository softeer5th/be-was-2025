package router;

import exception.ClientErrorException;
import exception.ErrorCode;
import handler.Handler;
import handler.StaticFileHandler;
import handler.UserRequestHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RequestRouter implements Router {

    private final Map<Pattern, Handler> routeMap;

    public RequestRouter() {
        routeMap = new HashMap<>();

        // 초기 경로와 핸들러 등록
        routeMap.put(Pattern.compile("^/user(/.*)?$"), new UserRequestHandler());
        routeMap.put(Pattern.compile("^.*\\.(html|css|js|svg)$"), new StaticFileHandler());
    }

    public Handler route(String path) {
        for (Map.Entry<Pattern, Handler> entry : routeMap.entrySet()) {
            if (entry.getKey().matcher(path).matches()) {
                return entry.getValue();
            }
        }
        throw new ClientErrorException(ErrorCode.NOT_ALLOWED_PATH);
    }
}
