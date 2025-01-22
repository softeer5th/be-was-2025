package webserver.response.body;

import util.FileUtil;
import webserver.enums.ContentType;
import webserver.exception.NotFound;

import java.io.*;
import java.util.Optional;

/**
 * File 을 body로 가지는 ResponseBody
 */
class FileBody extends ResponseBody {
    private final File file;

    /**
     * FileBody 생성자
     *
     * @param file body로 사용할 파일
     */
    public FileBody(File file) {
        this.file = file;
    }

    /**
     * 파일을 읽어서 클라이언트에게 전송
     *
     * @param out 클라이언트 OutputStream
     * @throws FileNotFoundException 파일을 찾을 수 없을 때 발생
     */
    @Override
    public void writeBody(OutputStream out) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            // 파일을 읽어서 클라이언트에게 전송
            in.transferTo(out);
        } catch (FileNotFoundException e) {
            throw new NotFound("파일을 찾을 수 없습니다.", e);
        }
    }

    @Override
    public Optional<Long> getContentLength() {
        return Optional.of(file.length());
    }

    @Override
    public Optional<ContentType> getContentType() {
        return Optional.of(ContentType.of(FileUtil.getFileExtension(file.getName())));
    }

    @Override
    public String toString() {
        return "FileBody{" +
               "file=" + file.getName() +
               '}';
    }
}
