package webserver.response.body;

import util.FileUtil;
import webserver.enums.ContentType;
import webserver.exception.NotFound;

import java.io.*;
import java.util.Optional;

// File 타입의 Body를 나타내는 클래스
class FileBody extends ResponseBody {
    private final File file;

    public FileBody(File file) {
        this.file = file;
    }

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
