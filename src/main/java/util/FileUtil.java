package util;


import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * 파일 관련 유틸리티
 */
public class FileUtil {

    /**
     * 리소스 폴더 내부 파일의 절대 경로를 반환한다.
     *
     * @param fileName 리소스 파일 이름
     * @return 파일의 절대 경로
     */
    public static Optional<String> getResourceAbsolutePath(String fileName) {
        URL resource = FileUtil.class.getClassLoader().getResource(fileName);
        if (resource == null) {
            return Optional.empty();
        }
        return Optional.of(resource.getFile());
    }

    /**
     * 파일 이름에서 확장자를 추출한다.
     *
     * @param fileName 파일 이름
     * @return 파일 확장자
     */
    public static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    /**
     * 경로를 합쳐서 반환한다.
     *
     * @param path  기본 경로.
     * @param paths 추가 경로. 경로 구분자(/, \)가 있으면 안된다
     * @return 합쳐진 경로
     */
    public static String joinPath(String path, String... paths) {
        return Paths.get(path, paths).toString();
    }

}
