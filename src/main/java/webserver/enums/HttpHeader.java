package webserver.enums;

public enum HttpHeader {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length"),
    HOST("Host"),
    LOCATION("Location"),
    COOKIE("Cookie"),
    SET_COOKIE("Set-Cookie");


    public final String value;

    HttpHeader(String value) {
        this.value = value;
    }

    public boolean equals(String value) {
        return this.value.equalsIgnoreCase(value);
    }
}

