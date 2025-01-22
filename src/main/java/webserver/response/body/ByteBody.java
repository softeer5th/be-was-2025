package webserver.response.body;

import webserver.enums.ContentType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

/**
 * Byte 배열을 body로 가지는 ResponseBody
 */
class ByteBody extends ResponseBody {
    private final byte[] body;
    private final ContentType contentType;

    /**
     * ByteBody 생성자
     *
     * @param body        body로 사용할 byte 배열
     * @param contentType body의 content type
     */
    public ByteBody(byte[] body, ContentType contentType) {
        this.body = body;
        this.contentType = contentType;
    }

    @Override
    public void writeBody(OutputStream out) throws IOException {
        out.write(body);
    }

    @Override
    public Optional<Long> getContentLength() {
        return Optional.of((long) body.length);
    }

    @Override
    public Optional<ContentType> getContentType() {
        return Optional.of(contentType);
    }
}
