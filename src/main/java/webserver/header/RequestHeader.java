package webserver.header;

import java.util.HashMap;
import java.util.Map;

// HTTP Header 정보를 담는 객체
public class RequestHeader extends AbstractHttpHeader {
    private final Map<String, String> cookies;

    public RequestHeader() {
        this.cookies = new HashMap<>();
    }

    public void addCookie(String name, String value) {
        cookies.put(name, value);
    }

    public void addCookies(Map<String, String> cookies) {
        this.cookies.putAll(cookies);
    }

    public String getCookie(String name) {
        return cookies.get(name);
    }


    @Override
    public String toString() {
        return "RequestHeader{" +
                "cookies=" + cookies +
                ", headers=" + headers +
                '}';
    }


}
