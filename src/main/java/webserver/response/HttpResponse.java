package webserver.response;

import util.FileUtil;
import webserver.enums.ContentType;
import webserver.enums.HttpHeader;
import webserver.enums.HttpStatusCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

// HTTP 응답에 대한 정보를 담는 객체
public class HttpResponse {
    private final HttpStatusCode statusCode;
    private final Map<String, String> headers;
    private Body body;

    public HttpResponse(HttpStatusCode statusCode) {
        this(statusCode, new HashMap<>());
    }

    public HttpResponse(HttpStatusCode statusCode, Map<String, String> headers) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = new EmptyBody();
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    Body getBody() {
        return body;
    }

    public HttpResponse setBody(String string) {
        this.body = new StringBody(string);
        setContentHeaders();
        return this;
    }

    public HttpResponse setBody(File file) {
        this.body = new FileBody(file);
        setContentHeaders();
        return this;
    }

    public HttpResponse setHeader(String key, String value) {
        headers.put(key, value);
        return this;
    }

    private void setContentHeaders() {
        headers.put(HttpHeader.CONTENT_LENGTH.value, body.getContentLength().toString());
        body.getContentType()
                .ifPresent(contentType -> headers.put(HttpHeader.CONTENT_TYPE.value, contentType.mimeType));

    }


    abstract static class Body {
        abstract void writeBody(OutputStream out);

        abstract Long getContentLength();

        abstract Optional<ContentType> getContentType();
    }

    private static class StringBody extends Body {
        private final String body;

        public StringBody(String body) {
            this.body = body;
        }

        @Override
        void writeBody(OutputStream out) {
            try {
                out.write(body.getBytes());
            } catch (Exception e) {
                throw new IllegalStateException("응답 데이터를 전송하는데 실패했습니다.", e);
            }
        }

        @Override
        Long getContentLength() {
            return (long) body.getBytes().length;
        }

        @Override
        Optional<ContentType> getContentType() {
            return Optional.of(ContentType.TEXT_PLAIN);
        }
    }

    private static class FileBody extends Body {
        private final File file;

        public FileBody(File file) {
            this.file = file;
        }

        @Override
        void writeBody(OutputStream out) {
            try (FileInputStream in = new FileInputStream(file)) {
                in.transferTo(out);
            } catch (IOException e) {
                throw new IllegalStateException("파일 전송에 실패했습니다.", e);
            }
        }

        @Override
        Long getContentLength() {
            return file.length();
        }

        @Override
        Optional<ContentType> getContentType() {
            return Optional.of(ContentType.of(FileUtil.getFileExtension(file.getName())));
        }
    }

    private static class EmptyBody extends Body {

        @Override
        void writeBody(OutputStream out) {
            // do nothing
        }

        @Override
        Long getContentLength() {
            return 0L;
        }

        @Override
        Optional<ContentType> getContentType() {
            return Optional.empty();
        }
    }
}
