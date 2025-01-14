package enums;

public enum HttpHeader {
    LOCATION("Location"),
    CONTENT_LENGTH("content-length"),
    SET_COOKIE("set-cookie"),
    COOKIE("cookie");

    private final String name;

    HttpHeader(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
