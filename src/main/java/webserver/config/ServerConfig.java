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
    private final String staticDirectory;
    // src/main/resources 폴더를 기준으로 template 폴더까지의 상대 경로
    private final String templateDirectory;
    // 기본 페이지 파일 이름
    private final String defaultPageFileName;
    // 헤더의 최대 크기. byte 단위
    private final Integer maxHeaderSize;
    // jdbc url
    private final String jdbcUrl;
    // jdbc username
    private final String username;
    // jdbc password
    private final String password;

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
            staticDirectory = props.getProperty("staticDirectory");
            templateDirectory = props.getProperty("templateDirectory");
            defaultPageFileName = props.getProperty("defaultPageFileName");
            maxHeaderSize = Integer.parseInt(props.getProperty("maxHeaderSize"));
            jdbcUrl = props.getProperty("jdbcUrl");
            username = props.getProperty("username");
            password = props.getProperty("password");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getStaticDirectory() {
        return staticDirectory;
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

    public String getTemplateDirectory() {
        return templateDirectory;
    }

    public String getJdbcUrl() {
        return jdbcUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
