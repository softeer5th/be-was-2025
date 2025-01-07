package webserver.response.body;

import webserver.enums.ContentType;

import java.io.OutputStream;
import java.util.Optional;

class EmptyBody extends Body {

    @Override
    public void writeBody(OutputStream out) {
        // do nothing
    }

    @Override
    public Long getContentLength() {
        return 0L;
    }

    @Override
    public Optional<ContentType> getContentType() {
        return Optional.empty();
    }
}
