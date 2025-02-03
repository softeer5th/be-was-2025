package webserver.enums;

/**
 * HTTP 헤더를 나타내는 enum
 */
public enum HttpHeader {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    HOST("Host"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie"),
    CONTENT_DISPOSITION("Content-Disposition");


    public final String value;

    HttpHeader(String value) {
        this.value = value;
    }

    /**
     * 헤더 이름을 이용해 일치 여부를 반환
     *
     * @param value 헤더 이름
     * @return 일치 여부. 대소문자 무시
     */
    public boolean equals(String value) {
        return this.value.equalsIgnoreCase(value);
    }
}

