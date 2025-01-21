package util;

import handler.*;

import java.util.concurrent.ConcurrentHashMap;

public class StaticResourcePathPool {

    private final ConcurrentHashMap<String, Handler> staticResourceMap = new ConcurrentHashMap<>();

    private static final StaticResourcePathPool instance = new StaticResourcePathPool();

    private static final DefaultStaticResourceHandler defaultHandler = new DefaultStaticResourceHandler();

    private StaticResourcePathPool() {
       initStaticResourcePath();
    }

    private void initStaticResourcePath() {
        staticResourceMap.put("/", new HomeHandler());
        staticResourceMap.put("/main", new MainHandler());
        staticResourceMap.put("/mypage", new MypageHandler());
    }

    public Handler getHandler(String path) {
        return staticResourceMap.getOrDefault(path, defaultHandler);
    }

    public static StaticResourcePathPool getInstance() {
        return instance;
    }
}
