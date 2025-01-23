package util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Optional;

/**
 * 파일 관련 유틸리티
 */
public class FileUtil {

    private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

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
     * 리소스 폴더 내부에 파일을 생성한다
     *
     * @param fileName 생성할 파일 이름
     * @return 생성된 파일. 파일이 이미 존재하면 Optional.empty()
     * @throws IllegalArgumentException 파일 생성 실패
     */
    public static Optional<File> createResourceFile(String fileName) {
        URL resource = FileUtil.class.getClassLoader().getResource("");
        log.debug("resource: {}", resource);
        log.debug("filename: {}", fileName);
        if (resource == null) {
            return Optional.empty();
        }
        String resourcePath = resource.getPath();
        File newFile = new File(FileUtil.joinPath(resourcePath, fileName));
        log.debug("filePath: {}", newFile.getAbsolutePath());
        try {
            // 경로상 폴더가 없으면 생성
            File parentDir = newFile.getParentFile();
            if (!parentDir.exists()) {
                parentDir.mkdirs();
            }
            if (newFile.createNewFile()) {
                return Optional.of(newFile);
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
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
