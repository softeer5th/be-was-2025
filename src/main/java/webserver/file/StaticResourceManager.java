package webserver.file;

import util.FileUtil;
import webserver.config.ServerConfig;

import java.io.File;
import java.net.URL;
import java.util.Optional;


// src/main/resources/static 폴더 내의 파일 관련된 메서드를 제공하는 클래스
public class StaticResourceManager {

    private final String staticResourceDirectory;

    public StaticResourceManager(ServerConfig config) {
        this.staticResourceDirectory = config.getStaticResourceDirectory();
    }


    // src/main/resources/static 폴더의 파일을 가져오는 메서드
    public Optional<File> getFile(String filePath) {
        String absolutePath = getAbsolutePath(filePath);
        if (absolutePath == null)
            return Optional.empty();
        return Optional.of(new File(absolutePath));
    }

    //
    public boolean isDirectory(String filePath) {
        String absolutePath = getAbsolutePath(filePath);
        if (absolutePath == null)
            return false;
        return new File(absolutePath).isDirectory();
    }

    private String getAbsolutePath(String filePath) {
        String staticFileRelativePath = FileUtil.joinPath(staticResourceDirectory, filePath);
        URL resource = getClass().getClassLoader().getResource(staticFileRelativePath);
        if (resource == null)
            return null;
        return resource.getFile();
    }
}
