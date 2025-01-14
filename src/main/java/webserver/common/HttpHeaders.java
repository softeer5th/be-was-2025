package webserver.common;

import webserver.enums.HttpHeader;

import java.util.HashMap;
import java.util.Map;

// HTTP Header 정보를 담는 객체
public class HttpHeaders {
    private final Map<String, String> headers;

    public HttpHeaders() {
        this.headers = new HashMap<>();
    }

    // HTTP Header name 의 case-insensitive 를 위해 소문자를 사용
    private String normalizeHeaderName(String name) {
        return name.toLowerCase();
    }

    public void setHeader(String name, String value) {
        name = normalizeHeaderName(name);
        headers.put(name, value);
    }

    public void setHeader(HttpHeader headerName, String value) {
        setHeader(headerName.value, value);
    }

    public String getHeader(String name) {
        name = normalizeHeaderName(name);
        return headers.get(name);
    }

    public String getHeader(HttpHeader headerName) {
        return getHeader(headerName.value);
    }

    public boolean containsHeader(HttpHeader headerName) {
        return containsHeader(headerName.value);
    }

    public boolean containsHeader(String name) {
        name = normalizeHeaderName(name);
        return headers.containsKey(name);
    }

    // HTTP Header name 을 소문자에서 일반적인 형태로 변환
    public Map<String, String> getFormattedHeaders() {
        Map<String, String> formattedHeaders = new HashMap<>();
        for (Map.Entry<String, String> header : headers.entrySet()) {
            String formattedHeaderName = HttpHeader.valueOf(header.getKey()).value;
            formattedHeaders.put(formattedHeaderName, header.getValue());
        }
        return formattedHeaders;
    }

    @Override
    public String toString() {
        return "HttpHeaders{" +
                "headers=" + getFormattedHeaders() +
                '}';
    }
}
