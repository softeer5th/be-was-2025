package webserver.header;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// HTTP Request Header 정보를 담는 객체
public class RequestHeader extends AbstractHttpHeader {
    private final Map<String, Cookie> cookies;

    public RequestHeader() {
        this.cookies = new HashMap<>();
    }

    public void addCookie(Cookie cookie) {
        // 중복된 이름이 쿠키가 들어오면 덮어쓰기
        cookies.put(cookie.getName(), cookie);
    }

    public void addCookies(List<Cookie> cookies) {
        cookies.forEach(this::addCookie);
    }

    public Optional<Cookie> getCookie(String name) {
        return Optional.ofNullable(cookies.get(name));
    }


    @Override
    public String toString() {
        return "RequestHeader{" +
                "cookies=" + cookies +
                ", headers=" + headers +
                '}';
    }


}
