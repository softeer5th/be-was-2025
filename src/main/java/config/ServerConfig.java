package config;

import router.RequestRouter;
import router.Router;

public class ServerConfig {
    private final int port;
    private final int threadPoolSize;
    private final Router router;

    public ServerConfig() {
        this.port = 8080;
        this.threadPoolSize = Runtime.getRuntime().availableProcessors();
        this.router = new RequestRouter();
    }

    public int getPort() {
        return port;
    }

    public int getThreadPoolSize() {
        return threadPoolSize;
    }

    public Router getRouter() {
        return router;
    }
}
