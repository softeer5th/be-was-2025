package router;

import exception.ClientErrorException;
import exception.ErrorCode;
import handler.*;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class RequestRouter implements Router {

    private final Map<Pattern, Handler> routeMap;

    public RequestRouter() {
        routeMap = new HashMap<>();

        // 초기 경로와 핸들러 등록
        routeMap.put(Pattern.compile("^/user(/.*)?$"), new UserRequestHandler());
        routeMap.put(Pattern.compile("^/board(/.*)?$"), new BoardHandler());
        // 루트 경로의 index.html과 mypage/ 하위의 index.html 제외
        routeMap.put(Pattern.compile("^/(?!mypage/.*index\\.html$|index\\.html$|article/index\\.html$).*\\.(html|css|js|svg|ico)$"), new StaticFileHandler());
        routeMap.put(Pattern.compile("^/$"), new HomeHandler());
        routeMap.put(Pattern.compile("^/index.html$"), new DynamicHomeHandler());
        // 마이페이지, registration, login
        routeMap.put(Pattern.compile("^/(mypage|login|registration|article|main)$"),new RedirectHandler());
        routeMap.put(Pattern.compile("^/(mypage/index.html|article/index.html)$"), new DynamicFileHandler());
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
