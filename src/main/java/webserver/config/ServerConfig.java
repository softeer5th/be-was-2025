package webserver.config;


import util.FileUtil;
import webserver.enums.HttpVersion;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * 서버 설정을 담당하는 클래스
 */
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
    // 파일 업로드 폴더
    private final String uploadDirectory;

    /**
     * 기본 설정 파일을 읽어와 설정을 초기화한다
     */
    public ServerConfig() {
        this(DEFAULT_PROPERTIES_FILE_PATH);
    }

    /**
     * 설정 파일을 읽어와 설정을 초기화한다
     *
     * @param propertiesFilePath 설정 파일 경로. properties 파일만 지원한다
     */
    public ServerConfig(String propertiesFilePath) {
        Properties props = new Properties();
        String propertiesAbsolutePath = FileUtil.getResourceAbsolutePath(propertiesFilePath).orElseThrow();
        try (InputStream in = new FileInputStream(propertiesAbsolutePath)) {
            props.load(in);
            supportedHttpVersions = Arrays.stream(props.getProperty("supportedHttpVersions").split(",")).map(HttpVersion::of).toList();
            port = Integer.parseInt(props.getProperty("port"));
            threadPoolSize = Integer.parseInt(props.getProperty("threadPoolSize"));
            staticDirectory = props.getProperty("staticDirectory");
            uploadDirectory = props.getProperty("uploadDirectory");
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

    /**
     * static 파일들이 위치한 디렉토리 경로를 반환한다
     *
     * @return static 파일들이 위치한 디렉토리 경로. resources 폴더를 기준으로 상대 경로
     */
    public String getStaticDirectory() {
        return staticDirectory;
    }

    /**
     * 서버에서 지원하는 HTTP 버전들을 반환한다
     *
     * @return 서버에서 지원하는 HTTP 버전들
     */
    public List<HttpVersion> getSupportedHttpVersions() {
        return supportedHttpVersions;
    }

    /**
     * 서버에서 사용할 포트 번호를 반환한다
     *
     * @return 서버에서 사용할 포트 번호
     */
    public Integer getPort() {
        return port;
    }

    /**
     * 서버에서 사용할 스레드 풀 크기를 반환한다
     *
     * @return 서버에서 사용할 스레드 풀 크기
     */
    public Integer getThreadPoolSize() {
        return threadPoolSize;
    }

    /**
     * 기본 페이지 파일 이름을 반환한다
     *
     * @return 기본 페이지 파일 이름
     */
    public String getDefaultPageFileName() {
        return defaultPageFileName;
    }

    /**
     * 헤더의 최대 크기를 반환한다
     *
     * @return 헤더의 최대 크기 (byte 단위)
     */
    public Integer getMaxHeaderSize() {
        return maxHeaderSize;
    }

    /**
     * template 파일들이 위치한 디렉토리 경로를 반환한다
     *
     * @return template 파일들이 위치한 디렉토리 경로. resources 폴더를 기준으로 상대 경로
     */
    public String getTemplateDirectory() {
        return templateDirectory;
    }

    /**
     * jdbc url을 반환한다
     *
     * @return jdbc url
     */
    public String getJdbcUrl() {
        return jdbcUrl;
    }

    /**
     * jdbc username을 반환한다
     *
     * @return jdbc username
     */
    public String getUsername() {
        return username;
    }

    /**
     * jdbc password를 반환한다
     *
     * @return jdbc password
     */
    public String getPassword() {
        return password;
    }

    public String getUploadDirectory() {
        return uploadDirectory;
    }
}
