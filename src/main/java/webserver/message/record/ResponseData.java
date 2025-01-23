package webserver.message.record;

import webserver.enumeration.HTTPContentType;
import webserver.enumeration.HTTPStatusCode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseData<T> {
    public static class ResponseDataBuilder<T> {
        T data;
        Map<String, String> headers = new HashMap<>();
        Map<String, SetCookieRecord> setCookies = new HashMap<>();
        HTTPStatusCode status;
        HTTPContentType contentType = HTTPContentType.APPLICATION_JSON;

        public ResponseDataBuilder() {}

        public ResponseDataBuilder<T> data(T data) {
            this.data = data;
            return this;
        }
        public ResponseDataBuilder<T> status(HTTPStatusCode status) {
            this.status = status;
            return this;
        }
        public ResponseDataBuilder<T> contentType(HTTPContentType contentType) {
            this.contentType = contentType;
            return this;
        }
        public ResponseDataBuilder<T> addHeader(String key, String value) {
            headers.put(key, value);
            return this;
        }
        public ResponseDataBuilder<T> setCookies(SetCookieRecord setCookie) {
            setCookies.put(setCookie.getName(), setCookie);
            return this;
        }

        public ResponseData<T> redirect(String location) {
            return this
                    .status(HTTPStatusCode.FOUND)
                    .addHeader("Location", location)
                    .build();
        }
        public ResponseData<T> ok(T data) {
            return this.data(data)
                    .status(HTTPStatusCode.OK)
                    .build();
        }

        public ResponseData<T> build() {
            return new ResponseData<>(data, status, contentType, headers, setCookies);
        }
    }

    final T data;
    final Map<String, String> headers;
    final Map<String, SetCookieRecord> setCookies;
    final HTTPStatusCode status;
    final HTTPContentType contentType;

    public ResponseData(
            T data,
            HTTPStatusCode status,
            HTTPContentType contentType,
            Map<String, String> headers,
            Map<String, SetCookieRecord> setCookies
    ) {
        this.data = data;
        this.headers = headers;
        this.setCookies = setCookies;
        this.status = status;
        this.contentType = contentType;
    }

    public static <T> ResponseData<T> ok(T data) {
        return new ResponseData.ResponseDataBuilder<T>()
                .ok(data);
    }

    public static ResponseData<String> redirect(String location) {
        return new ResponseDataBuilder<String>()
                .redirect(location);
    }

    public HTTPStatusCode getStatus() {
        return status;
    }

    public Map<String, String> getHeaders() {
        return this.headers;
    }

    public Map<String, SetCookieRecord> getSetCookies() {
        return this.setCookies;
    }

    public T getData() {
        return this.data;
    }

    public HTTPContentType getContentType() {
        return this.contentType;
    }
}
