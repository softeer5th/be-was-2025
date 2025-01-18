package http;

public enum HttpHeader {
    CONTENT_TYPE("content-type"),
    CONTENT_LENGTH("content-length"),
    SET_COOKIE("set-cookie"),
    COOKIE("cookie"),
    LOCATION("location");



    private final String headerName;

    HttpHeader(String headerName) {
        this.headerName = headerName;
    }

    public String getHeaderName() {
        return this.headerName;
    }
}
