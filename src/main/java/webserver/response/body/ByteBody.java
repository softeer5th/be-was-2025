package webserver.response.body;

import webserver.enums.ContentType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

class ByteBody extends ResponseBody {
    private final byte[] body;
    private final ContentType contentType;

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
