package webserver.response.body;

import webserver.enums.ContentType;

import java.io.OutputStream;
import java.util.Optional;

// 빈 Body를 나타내는 클래스
class EmptyBody extends ResponseBody {

    @Override
    public void writeBody(OutputStream out) {
        // do nothing
    }

    @Override
    public Optional<Long> getContentLength() {
        return Optional.empty();
    }

    @Override
    public Optional<ContentType> getContentType() {
        return Optional.empty();
    }

    @Override
    public String toString() {
        return "EmptyBody{}";
    }
}
