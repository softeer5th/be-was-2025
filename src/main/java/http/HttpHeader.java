package http;

public enum HttpHeader {
    CONTENT_TYPE("content-type"),
    CONTENT_LENGTH("content-length");

    private final String key;

    HttpHeader(String key) {
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }
}
