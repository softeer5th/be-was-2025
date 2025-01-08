package webserver.response.body;

import util.FileUtil;
import webserver.enums.ContentType;
import webserver.exception.InternalServerError;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

// File 타입의 Body를 나타내는 클래스
class FileBody extends Body {
    private final File file;

    public FileBody(File file) {
        this.file = file;
    }

    @Override
    public void writeBody(OutputStream out) {
        try (FileInputStream in = new FileInputStream(file)) {
            // 파일을 읽어서 클라이언트에게 전송
            in.transferTo(out);
        } catch (IOException e) {
            throw new InternalServerError("파일 전송에 실패했습니다.", e);
        }
    }

    @Override
    public Long getContentLength() {
        return file.length();
    }

    @Override
    public Optional<ContentType> getContentType() {
        return Optional.of(ContentType.of(FileUtil.getFileExtension(file.getName())));
    }
}
