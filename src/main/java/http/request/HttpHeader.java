package http.request;

public enum HttpHeader {
    CONTENT_TYPE("content-type"), CONTENT_LENGTH("content-length"), COOKIE("cookie");

    String name;

    HttpHeader(String name) {
        this.name = name;
    }

    public String getName(){
        return this.name;
    }
}
