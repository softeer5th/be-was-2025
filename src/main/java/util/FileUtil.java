package util;


import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

public class FileUtil {

    public static Optional<String> getResourceAbsolutePath(String fileName) {
        URL resource = FileUtil.class.getClassLoader().getResource(fileName);
        if (resource == null) {
            return Optional.empty();
        }
        return Optional.of(resource.getFile());
    }

    // 파일 이름에서 확장자 추출
    public static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    // 여러 경로를 합쳐서 하나의 경로로 반환
    public static String joinPath(String path, String... paths) {
        return Paths.get(path, paths).toString();
    }

}
