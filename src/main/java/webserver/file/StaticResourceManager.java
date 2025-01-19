package webserver.file;

import util.FileUtil;

import java.io.File;
import java.util.Optional;


// src/main/resources/static 폴더 내의 파일 관련된 메서드를 제공하는 클래스
public class StaticResourceManager {

    private final String staticDirectory;

    public StaticResourceManager(String staticDirectory) {
        this.staticDirectory = staticDirectory;
    }


    // static 폴더의 파일을 가져오는 메서드
    public Optional<File> getFile(String filePath) {
        return getAbsolutePath(filePath).map(File::new);
    }

    // 존재하면서 디렉터리인지 확인하는 메서드
    public boolean isDirectory(String filePath) {
        Optional<String> absolutePath = getAbsolutePath(filePath);
        return absolutePath.map(File::new).map(File::isDirectory).orElse(false);
    }


    // staic 폴더 기준의 상대경로를 절대경로로 변환하는 메서드
    private Optional<String> getAbsolutePath(String filePath) {
        String staticFileRelativePath = FileUtil.joinPath(staticDirectory, filePath);
        return FileUtil.getResourceAbsolutePath(staticFileRelativePath);
    }
}
