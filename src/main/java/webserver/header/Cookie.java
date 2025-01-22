package webserver.header;

import java.util.Objects;

/**
 * Cookie 헤더와 관련된 정보
 */
public class Cookie {
    private final String name;
    private final String value;

    /**
     * Cookie 생성자
     *
     * @param name  Cookie 이름
     * @param value Cookie 값
     */
    public Cookie(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Cookie cookie = (Cookie) o;
        return Objects.equals(name, cookie.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
