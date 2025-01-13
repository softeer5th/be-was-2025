package webserver.enums;

public enum ParsingConstant {
    CRLF("\r\n"),
    LF("\n"),
    SP(" "),
    HTTP_LINE_SEPARATOR("\r?\n"),
    HTTP_HEADERS_END_DELIMITER("\r?\n\r?\n"),
    REQUEST_LINE_SEPARATOR(" "),
    HEADER_KEY_SEPARATOR(":"),
    HEADER_VALUES_SEPARATOR(","),
    QUERY_PARAMETER_SEPARATOR("&"),
    QUERY_KEY_VALUE_SEPARATOR("="),
    QUERY_DEFAULT_VALUE(" ");

    public static final int MAX_HEADER_SIZE = 8192;
    public final String value;

    ParsingConstant(String value) {
        this.value = value;
    }


}
