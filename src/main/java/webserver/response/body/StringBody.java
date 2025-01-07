package webserver.response.body;

import webserver.enums.ContentType;

import java.io.OutputStream;
import java.util.Optional;

class StringBody extends Body {
    private final String body;

    public StringBody(String body) {
        this.body = body;
    }

    @Override
    public void writeBody(OutputStream out) {
        try {
            out.write(body.getBytes());
        } catch (Exception e) {
            throw new IllegalStateException("응답 데이터를 전송하는데 실패했습니다.", e);
        }
    }

    @Override
    public Long getContentLength() {
        return (long) body.getBytes().length;
    }

    @Override
    public Optional<ContentType> getContentType() {
        return Optional.of(ContentType.TEXT_PLAIN);
    }
}
