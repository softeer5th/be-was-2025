package webserver.file;

import util.FileUtil;
import webserver.request.FileUploader;

import java.io.*;
import java.util.Optional;
import java.util.UUID;


/**
 * static 폴더 내의 파일을 관리하는 클래스
 */
public class StaticResourceManager implements FileUploader {

    private final String staticDirectory;
    private final String uploadDirectory;

    /**
     * static 폴더 경로를 받아 생성
     *
     * @param staticDirectory static 폴더 경로
     */
    public StaticResourceManager(String staticDirectory, String uploadDirectory) {
        this.staticDirectory = staticDirectory;
        this.uploadDirectory = uploadDirectory;
    }

    /**
     * static 폴더의 파일을 가져오는 메서드
     *
     * @param filePath 파일 경로
     * @return 파일 객체. 파일이 존재하지 않으면 Optional.empty()
     */
    public Optional<File> getFile(String filePath) {
        return getAbsolutePath(filePath).map(File::new);
    }

    /**
     * static 폴더의 경로에 폴더가 존재하는지 확인하는 메서드
     *
     * @param filePath 폴더 경로
     * @return 폴더가 존재하면 true, 파일이거나 없으면 false
     */
    public boolean isDirectory(String filePath) {
        Optional<String> absolutePath = getAbsolutePath(filePath);
        return absolutePath.map(File::new).map(File::isDirectory).orElse(false);
    }

    @Override
    public String uploadFile(String filename, byte[] body) {
        String urlPath = FileUtil.joinPath(uploadDirectory, makeUniqueFilename(filename));
        String filePath = FileUtil.joinPath(staticDirectory, urlPath);
        File file = FileUtil.createResourceFile(filePath)
                .orElseThrow(() -> new IllegalArgumentException("파일이 이미 존재합니다"));
        // 파일 업로드
        try (InputStream in = new ByteArrayInputStream(body);
             FileOutputStream out = new FileOutputStream(file);) {
            in.transferTo(out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return "/" + urlPath;
    }

    @Override
    public void deleteFile(String path) {
        getAbsolutePath(path).ifPresent(t -> {
            new File(t).delete();
        });
    }

    private String makeUniqueFilename(String filename) {
        // UUID + 확장자 형태로 파일명 생성
        return UUID.randomUUID() + "." + FileUtil.getFileExtension(filename);
    }

    // staic 폴더 기준의 상대경로를 절대경로로 변환하는 메서드
    private Optional<String> getAbsolutePath(String filePath) {
        String staticFileRelativePath = FileUtil.joinPath(staticDirectory, filePath);
        return FileUtil.getResourceAbsolutePath(staticFileRelativePath);
    }
}
