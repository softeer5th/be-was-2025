package util;

import java.util.concurrent.ConcurrentHashMap;

public class PathPool {
    private ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
    private static PathPool instance = new PathPool();

    public PathPool() {
        map.put("/user/create", "");
    }

    public static PathPool getInstance() {
        return instance;
    }

    public String get(String path) {
        return map.get(path);
    }

}
