package webserver.config;


import util.FileUtil;
import webserver.enums.HttpVersion;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

// 서버의 설정을 담는 객체
public class ServerConfig {
    // 기본 설정 파일 경로
    private static final String DEFAULT_PROPERTIES_FILE_PATH = "server-config.properties";

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
    // 템플릿 파일의 확장자
    private final String templateFileExtension;

    public ServerConfig() {
        this(DEFAULT_PROPERTIES_FILE_PATH);
    }

    public ServerConfig(String propertiesFilePath) {
        Properties props = new Properties();
        String propertiesAbsolutePath = FileUtil.getResourceAbsolutePath(propertiesFilePath).orElseThrow();
        try (InputStream in = new FileInputStream(propertiesAbsolutePath)) {
            props.load(in);
            supportedHttpVersions = Arrays.stream(props.getProperty("supportedHttpVersions").split(",")).map(HttpVersion::of).toList();
            port = Integer.parseInt(props.getProperty("port"));
            threadPoolSize = Integer.parseInt(props.getProperty("threadPoolSize"));
            staticResourceDirectory = props.getProperty("staticResourceDirectory");
            defaultPageFileName = props.getProperty("defaultPageFileName");
            maxHeaderSize = Integer.parseInt(props.getProperty("maxHeaderSize"));
            templateFileExtension = props.getProperty("templateFileExtension");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    public String getTemplateFileExtension() {
        return templateFileExtension;
    }
}
