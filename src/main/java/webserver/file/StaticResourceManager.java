package webserver.file;

import util.FileUtil;
import webserver.config.ServerConfig;
import webserver.view.TemplateFileReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;


// src/main/resources/static 폴더 내의 파일 관련된 메서드를 제공하는 클래스
public class StaticResourceManager implements TemplateFileReader {

    private final String staticResourceDirectory;

    public StaticResourceManager(ServerConfig config) {
        this.staticResourceDirectory = config.getStaticResourceDirectory();
    }


    // static 폴더의 파일을 가져오는 메서드
    public Optional<File> getFile(String filePath) {
        String absolutePath = getAbsolutePath(filePath);
        if (absolutePath == null)
            return Optional.empty();
        return Optional.of(new File(absolutePath));
    }

    // 존재하면서 디렉터리인지 확인하는 메서드
    public boolean isDirectory(String filePath) {
        String absolutePath = getAbsolutePath(filePath);
        if (absolutePath == null)
            return false;
        return new File(absolutePath).isDirectory();
    }

    // static 폴더 내의 파일을 읽어서 String으로 반환하는 메서드
    public String read(String filePath) {
        File templateFile = getFile(filePath).orElseThrow(IllegalArgumentException::new);
        try (InputStream in = new FileInputStream(templateFile)) {
            return new String(in.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // staic 폴더 기준의 상대경로를 절대경로로 변환하는 메서드
    private String getAbsolutePath(String filePath) {
        String staticFileRelativePath = FileUtil.joinPath(staticResourceDirectory, filePath);
        URL resource = getClass().getClassLoader().getResource(staticFileRelativePath);
        if (resource == null)
            return null;
        return resource.getFile();
    }
}
