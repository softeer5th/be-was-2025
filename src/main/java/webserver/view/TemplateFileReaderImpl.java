package webserver.view;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static util.FileUtil.getResourceAbsolutePath;
import static util.FileUtil.joinPath;

/**
 * template 디렉터리에서 템플릿 파일을 읽어오는 클래스
 */
public class TemplateFileReaderImpl implements TemplateFileReader {
    private final String templateDirectory;

    /**
     * 생성자
     *
     * @param templateDirectory 템플릿 파일이 위치한 디렉터리 경로. resources 디렉터리 기준으로 상대경로
     */
    public TemplateFileReaderImpl(String templateDirectory) {
        this.templateDirectory = templateDirectory;
    }

    // template 폴더 내의 파일을 읽어서 String으로 반환하는 메서드
    @Override
    public String read(String filePath) {
        String absoluteFilePath = getResourceAbsolutePath(joinPath(templateDirectory, filePath)).orElseThrow(IllegalArgumentException::new);
        try (InputStream in = new FileInputStream(absoluteFilePath)) {
            return new String(in.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
