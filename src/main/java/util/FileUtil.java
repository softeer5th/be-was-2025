package util;


import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

public class FileUtil {
    public static final String STATIC_FOLDER = "static";

    // src/main/resources/static 폴더의 파일을 가져오는 메서드
    public static Optional<File> getFileInResources(String filePath) {
        String staticFilePath = Paths.get(STATIC_FOLDER, filePath).toString();
        URL resource = FileUtil.class.getClassLoader().getResource(staticFilePath);
        if (resource == null)
            return Optional.empty();
        return Optional.of(new File(resource.getFile()));
    }

    // 파일 이름에서 확장자 추출
    public static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }
}
