package webserver.config;


import webserver.enums.HttpVersion;

import java.util.List;

public class ServerConfig {

    private final List<HttpVersion> supportedHttpVersions;
    private final Integer port;
    private final Integer threadPoolSize;
    private final String staticResourceDirectory;
    private final String defaultPage;

    public ServerConfig() {
        supportedHttpVersions = List.of(HttpVersion.HTTP_1_1);
        port = 8080;
        threadPoolSize = 200;
        staticResourceDirectory = "static";
        defaultPage = "index.html";
    }

    public ServerConfig(List<HttpVersion> supportedHttpVersions, Integer port, Integer threadPoolSize, String staticResourceDirectory, String defaultPage) {
        this.supportedHttpVersions = supportedHttpVersions;
        this.port = port;
        this.threadPoolSize = threadPoolSize;
        this.staticResourceDirectory = staticResourceDirectory;
        this.defaultPage = defaultPage;
    }

    public String getStaticResourceDirectory() {
        return staticResourceDirectory;
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

    public String getDefaultPage() {
        return defaultPage;
    }
}
