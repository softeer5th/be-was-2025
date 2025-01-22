package enums;

/**
 * HTTP 헤더를 나타내는 열거형 클래스입니다.
 * 각 헤더 이름을 문자열로 매핑합니다.
 */
public enum HttpHeader {
    /**
     * HTTP Location 헤더
     */
    LOCATION("location"),

    /**
     * HTTP Content-Length 헤더
     */
    CONTENT_LENGTH("content-length"),

    /**
     * HTTP Set-Cookie 헤더
     */
    SET_COOKIE("set-cookie"),

    /**
     * HTTP Cookie 헤더
     */
    COOKIE("cookie");

    private final String name;

    /**
     * 열거형 인스턴스를 생성합니다.
     *
     * @param name HTTP 헤더의 이름
     */
    HttpHeader(String name) {
        this.name = name;
    }

    /**
     * HTTP 헤더 이름을 반환합니다.
     *
     * @return HTTP 헤더 이름
     */
    public String getName() {
        return name;
    }
}
