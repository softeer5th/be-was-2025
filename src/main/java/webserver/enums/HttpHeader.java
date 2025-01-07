package webserver.enums;

public enum HttpHeader {
    CONTENT_TYPE("Content-Type"),
    CONTENT_LENGTH("Content-Length");

    public final String value;

    HttpHeader(String value) {
        this.value = value;
    }

}

