package util;

import handler.Handler;
import handler.HomeHandler;
import handler.MainHandler;
import handler.MypageHandler;

import java.util.concurrent.ConcurrentHashMap;

public class StaticResourcePathPool {

    private final ConcurrentHashMap<String, Handler> staticResourceMap = new ConcurrentHashMap<>();

    private static final StaticResourcePathPool instance = new StaticResourcePathPool();

    private StaticResourcePathPool() {
       initStaticResourcePath();
    }

    private void initStaticResourcePath() {
        staticResourceMap.put("/", new HomeHandler());
        staticResourceMap.put("/main", new MainHandler());
        staticResourceMap.put("/mypage", new MypageHandler());
    }

    public Handler getHandler(String path) {
        return staticResourceMap.get(path);
    }

    public static StaticResourcePathPool getInstance() {
        return instance;
    }
}
