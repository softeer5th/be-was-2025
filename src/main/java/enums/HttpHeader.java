package enums;

public enum HttpHeader {
    LOCATION("Location"),
    CONTENT_LENGTH("content-length");

    private final String name;

    HttpHeader(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
