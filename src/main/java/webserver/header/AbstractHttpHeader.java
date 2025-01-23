package webserver.header;

import webserver.enums.HttpHeader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Http Header와 관련된 공통 기능을 제공하는 추상 클래스
 */
public abstract class AbstractHttpHeader {
    protected final Map<String, String> headers;

    public AbstractHttpHeader() {
        this.headers = new HashMap<>();
    }

    /**
     * Http Header를 설정한다.
     *
     * @param name  Header 이름. 대소문자 구분 없음
     * @param value Header 값
     */
    public void setHeader(String name, String value) {
        name = normalizeHeaderName(name);
        headers.put(name, value);
    }

    /**
     * Http Header를 반환한다
     *
     * @param name Header 이름. 대소문자 구분 없음
     * @return Header 값
     */
    public String getHeader(String name) {
        name = normalizeHeaderName(name);
        return headers.get(name);
    }

    /**
     * Http Header name 포함 여부를 반환한다
     *
     * @param headerName Header 이름
     * @return Header 포함 여부
     */
    public boolean containsHeader(HttpHeader headerName) {
        return containsHeader(headerName.value);
    }

    /**
     * Http Header name 포함 여부를 반환한다
     *
     * @param name Header 이름. 대소문자 구분 없음
     * @return Header 포함 여부
     */
    public boolean containsHeader(String name) {
        name = normalizeHeaderName(name);
        return headers.containsKey(name);
    }

    /**
     * 포함하고 있는 Http Header 이름을 반환한다
     *
     * @return Header 이름 Set
     */
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
