package webserver.response.body;

import webserver.enums.ContentType;
import webserver.exception.InternalServerError;

import java.io.OutputStream;
import java.util.Optional;

// String 타입의 Body를 나타내는 클래스
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
            throw new InternalServerError("응답 데이터를 전송하는데 실패했습니다.", e);
        }
    }

    @Override
    public Optional<Long> getContentLength() {
        return Optional.of((long) body.getBytes().length);
    }

    @Override
    public Optional<ContentType> getContentType() {
        return Optional.of(ContentType.TEXT_PLAIN);
    }

    @Override
    public String toString() {
        return "StringBody{" +
                "body='" + body + '\'' +
                '}';
    }
}
