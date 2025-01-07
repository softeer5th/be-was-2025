package webserver.response.body;

import util.FileUtil;
import webserver.enums.ContentType;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

class FileBody extends Body {
    private final File file;

    public FileBody(File file) {
        this.file = file;
    }

    @Override
    public void writeBody(OutputStream out) {
        try (FileInputStream in = new FileInputStream(file)) {
            in.transferTo(out);
        } catch (IOException e) {
            throw new IllegalStateException("파일 전송에 실패했습니다.", e);
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
