package webserver.response.body;

import webserver.enums.ContentType;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

// String 타입의 Body를 나타내는 클래스
class StringBody extends ResponseBody {
    private final String body;

    public StringBody(String body) {
        this.body = body;
    }

    @Override
    public void writeBody(OutputStream out) throws IOException {
        out.write(body.getBytes());
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
