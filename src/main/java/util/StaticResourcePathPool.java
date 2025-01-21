package util;

import handler.*;
import http.constant.HttpMethod;
import util.exception.NoSuchPathException;

import java.io.File;
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
        staticResourceMap.put("/article", new ArticleHandler());
    }

    public Handler getHandler(String path) {
        return staticResourceMap.getOrDefault(path, defaultHandler);
    }

    public boolean isAvailable(HttpMethod method, String path) {
        if (method != HttpMethod.GET) return false;

        try {
            File file = FileUtils.findFile(path);
        } catch (NoSuchPathException e) {
            return false;
        }
        return true;
    }

    public static StaticResourcePathPool getInstance() {
        return instance;
    }
}
