package webserver.enums;

public enum ParsingConstant {
    SP(" "),
    CR("\r"),
    LF("\n"),
    CRLF("\r\n"),
    CRLFCRLF("\r\n\r\n"),
    HTTP_LINE_SEPARATOR("\r?\n"),
    HTTP_HEADERS_END_DELIMITER("\r?\n\r?\n"),
    // Request Line 구분자는 공백문자가 1개 이상 올 수 있다. 사용 가능한 공백문자에는 SP, TAB, VT, FF, CR이 있다. (rfc9112#section-3)
    REQUEST_LINE_SEPARATOR("[ \\t\\v\\f\\r]+"),
    HEADER_KEY_SEPARATOR(":"),
    HEADER_VALUES_SEPARATOR(","),
    QUERY_PARAMETER_SEPARATOR("&"),
    QUERY_KEY_VALUE_SEPARATOR("="),
    QUERY_DEFAULT_VALUE(" "),
    FORM_URLENCODED_SEPARATOR("&"),
    FORM_URLENCODED_KEY_VALUE_SEPARATOR("="),
    FORM_URLENCODED_DEFAULT_KEY(""),
    FORM_URLENCODED_DEFAULT_VALUE(""),
    DEFAULT_CHARSET("UTF-8"),
    COOKIE_SEPARATOR(";"),
    COOKIE_KEY_VALUE_SEPARATOR("="),
    MULTIPART_BOUNDARY("boundary="),
    CONTENT_DISPOSITION_ATTRIBUTE_SEPARATOR(";"),
    CONTENT_DISPOSITION_ATTRIBUTE_KEY_VALUE_SEPARATOR("=");

    public final String value;

    ParsingConstant(String value) {
        this.value = value;
    }

    public boolean equals(String value) {
        return this.value.equals(value);
    }

    public boolean equals(int c) {
        return this.value.charAt(0) == c;
    }
}
