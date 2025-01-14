package webserver.header;

import java.util.ArrayList;
import java.util.List;

// HTTP Response Header 정보를 담는 객체
public class ResponseHeader extends AbstractHttpHeader {
    private final List<SetCookie> setCookies;

    public ResponseHeader() {
        this.setCookies = new ArrayList<>();
    }


    public void addSetCookie(SetCookie setCookie) {
        setCookies.add(setCookie);
    }

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
