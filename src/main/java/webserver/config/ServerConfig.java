package webserver.config;


import webserver.enums.HttpVersion;

import java.util.List;

public class ServerConfig {

    private final List<HttpVersion> supportedHttpVersions;
    private final Integer port;
    private final Integer threadPoolSize;

    public ServerConfig() {
        supportedHttpVersions = List.of(HttpVersion.HTTP_1_1);
        port = 8080;
        threadPoolSize = 200;
    }

    public ServerConfig(List<HttpVersion> supportedHttpVersions, Integer port, Integer threadPoolSize) {
        this.supportedHttpVersions = supportedHttpVersions;
        this.port = port;
        this.threadPoolSize = threadPoolSize;
    }

    public List<HttpVersion> getSupportedHttpVersions() {
        return supportedHttpVersions;
    }

    public Integer getPort() {
        return port;
    }

    public Integer getThreadPoolSize() {
        return threadPoolSize;
    }
}
