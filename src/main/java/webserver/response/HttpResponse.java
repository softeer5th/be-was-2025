package webserver.response;

import webserver.enums.HttpHeader;
import webserver.enums.HttpStatusCode;
import webserver.enums.HttpVersion;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// HTTP 응답에 대한 정보를 담는 객체
public class HttpResponse {
    public static final HttpVersion DEFAULT_VERSION = HttpVersion.HTTP_1_1;

    private final HttpVersion version;
    private final HttpStatusCode statusCode;
    private final Map<String, String> headers;
    private Body body;

    public HttpResponse(HttpStatusCode statusCode) {
        this(DEFAULT_VERSION, statusCode, new HashMap<>());
    }

    public HttpResponse(HttpStatusCode statusCode, Map<String, String> headers) {
        this(DEFAULT_VERSION, statusCode, headers);
    }

    public HttpResponse(HttpVersion version, HttpStatusCode statusCode, Map<String, String> headers) {
        this.version = version;
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = new EmptyBody();
    }

    public HttpVersion getVersion() {
        return version;
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
        setContentLength();
        return this;
    }

    public HttpResponse setBody(File file) {
        this.body = new FileBody(file);
        setContentLength();
        return this;
    }

    private void setContentLength() {
        headers.put(HttpHeader.CONTENT_LENGTH.value, body.getContentLength().toString());
    }

    abstract static class Body {
        abstract void writeBody(BufferedWriter out);

        abstract Long getContentLength();
    }

    private static class StringBody extends Body {
        private final String body;

        public StringBody(String body) {
            this.body = body;
        }

        @Override
        void writeBody(BufferedWriter out) {
            try {
                out.write(body);
            } catch (Exception e) {
                throw new IllegalStateException("응답 데이터를 전송하는데 실패했습니다.", e);
            }
        }

        @Override
        Long getContentLength() {
            return (long) body.getBytes().length;
        }
    }

    private static class FileBody extends Body {
        private final File file;

        public FileBody(File file) {
            this.file = file;
        }

        @Override
        void writeBody(BufferedWriter out) {
            try (FileReader in = new FileReader(file)) {
                in.transferTo(out);
            } catch (IOException e) {
                throw new IllegalStateException("파일 전송에 실패했습니다.", e);
            }
        }

        @Override
        Long getContentLength() {
            return file.length();
        }
    }

    private static class EmptyBody extends Body {

        @Override
        void writeBody(BufferedWriter out) {
            // do nothing
        }

        @Override
        Long getContentLength() {
            return 0L;
        }
    }
}
