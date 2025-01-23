package webserver.header;

import java.util.ArrayList;
import java.util.List;

/**
 * HTTP Response Header 정보를 담는 객체
 */
public class ResponseHeader extends AbstractHttpHeader {
    private final List<SetCookie> setCookies;

    public ResponseHeader() {
        this.setCookies = new ArrayList<>();
    }

    /**
     * Set-Cookie 헤더를 추가한다.
     * 중복 가능하다
     *
     * @param setCookie 추가할 Set-Cookie 헤더
     */
    public void addSetCookie(SetCookie setCookie) {
        setCookies.add(setCookie);
    }

    /**
     * Set-Cookie 헤더를 반환한다.
     *
     * @return Set-Cookie 헤더 목록
     */
    public List<SetCookie> getSetCookies() {
        return setCookies;
    }

    @Override
    public String toString() {
        return "ResponseHeader{" +
               "setCookies=" + setCookies +
               ", headers=" + headers +
               '}';
    }
}
