package webserver.config;


import webserver.enums.HttpVersion;

import java.util.List;

// 서버의 설정을 담는 객체
public class ServerConfig {
    // 서버에서 지원하는 HTTP 버전들
    private final List<HttpVersion> supportedHttpVersions;
    // 기본으로 사용할 포트 번호
    private final Integer port;
    // 서버에서 사용할 스레드 풀 크기
    private final Integer threadPoolSize;
    // src/main/resources 폴더를 기준으로 static 폴더까지의 상대 경로
    private final String staticResourceDirectory;
    // 기본 페이지 파일 이름
    private final String defaultPageFileName;
    // 헤더의 최대 크기. byte 단위
    private final Integer maxHeaderSize;

    public ServerConfig() {
        supportedHttpVersions = List.of(HttpVersion.HTTP_1_1);
        port = 8080;
        threadPoolSize = 20;
        staticResourceDirectory = "static";
        defaultPageFileName = "index.html";
        maxHeaderSize = 8192;
    }

    public ServerConfig(List<HttpVersion> supportedHttpVersions, Integer port, Integer threadPoolSize, String staticResourceDirectory, String defaultPage, Integer maxHeaderSize) {
        this.supportedHttpVersions = supportedHttpVersions;
        this.port = port;
        this.threadPoolSize = threadPoolSize;
        this.staticResourceDirectory = staticResourceDirectory;
        this.defaultPageFileName = defaultPage;
        this.maxHeaderSize = maxHeaderSize;
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

    public String getDefaultPageFileName() {
        return defaultPageFileName;
    }

    public Integer getMaxHeaderSize() {
        return maxHeaderSize;
    }
}
