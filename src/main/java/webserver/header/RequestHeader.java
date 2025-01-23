package webserver.header;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * HTTP Request Header 정보를 담는 객체
 */
public class RequestHeader extends AbstractHttpHeader {
    private final Map<String, Cookie> cookies;

    public RequestHeader() {
        this.cookies = new HashMap<>();
    }

    /**
     * 쿠키를 추가한다.
     * 중복되는 이름의 쿠키가 있으면 덮어쓴다.
     *
     * @param cookie 추가할 쿠키
     */
    public void addCookie(Cookie cookie) {
        // 중복된 이름이 쿠키가 들어오면 덮어쓰기
        cookies.put(cookie.getName(), cookie);
    }

    /**
     * 쿠키들을 추가한다.
     * 중복되는 이름의 쿠키가 있으면 덮어쓴다.
     *
     * @param cookies 추가할 쿠키들
     */
    public void addCookies(List<Cookie> cookies) {
        cookies.forEach(this::addCookie);
    }

    /**
     * 쿠키를 반환한다.
     *
     * @param name 쿠키 이름
     * @return 쿠키. 없으면 Optional.empty()
     */
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
