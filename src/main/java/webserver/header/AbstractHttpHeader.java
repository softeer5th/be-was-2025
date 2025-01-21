package webserver.header;

import webserver.enums.HttpHeader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

// Http Header와 관련된 공통 기능을 제공하는 추상 클래스
public abstract class AbstractHttpHeader {
    protected final Map<String, String> headers;

    public AbstractHttpHeader() {
        this.headers = new HashMap<>();
    }

    public void setHeader(String name, String value) {
        name = normalizeHeaderName(name);
        headers.put(name, value);
    }


    public String getHeader(String name) {
        name = normalizeHeaderName(name);
        return headers.get(name);
    }

    public boolean containsHeader(HttpHeader headerName) {
        return containsHeader(headerName.value);
    }

    public boolean containsHeader(String name) {
        name = normalizeHeaderName(name);
        return headers.containsKey(name);
    }

    public Set<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public String toString() {
        return "HttpHeaders{" +
                "headers=" + headers +
                '}';
    }

    // HTTP Header name 의 case-insensitive 를 위해 소문자를 사용
    protected String normalizeHeaderName(String name) {
        return name.toLowerCase();
    }

}
