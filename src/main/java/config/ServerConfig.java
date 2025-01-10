package config;

import enums.HttpVersion;
import router.RequestRouter;
import router.Router;

import java.util.List;

public class ServerConfig {
    // 모든 필드를 static으로 선언하여 단 하나의 설정을 사용
    private static final int port = 8080;
    private static final int threadPoolSize = Runtime.getRuntime().availableProcessors();
    private static final Router router = new RequestRouter();
    private static final List<HttpVersion> supportedHttpVersions = List.of(HttpVersion.HTTP1_1);

    // static 메서드를 통해 값에 접근
    public static int getPort() {
        return port;
    }

    public static int getThreadPoolSize() {
        return threadPoolSize;
    }

    public static Router getRouter() {
        return router;
    }

    public static List<HttpVersion> getSupportedHttpVersions() {
        return supportedHttpVersions;
    }
}

