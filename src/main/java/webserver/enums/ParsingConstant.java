package webserver.enums;

public enum ParsingConstant {
    CRLF("\r\n"),
    LF("\n"),
    SP(" "),
    HTTP_LINE_SEPARATOR("\r?\n"),
    HTTP_HEADERS_END_DELIMITER("\r?\n\r?\n"),
    // Request Line 구분자는 공백문자가 1개 이상 올 수 있다. 사용 가능한 공백문자에는 SP, TAB, VT, FF, CR이 있다. (rfc9112#section-3)
    REQUEST_LINE_SEPARATOR("[ \\t\\v\\f\\r]+"),
    HEADER_KEY_SEPARATOR(":"),
    HEADER_VALUES_SEPARATOR(","),
    QUERY_PARAMETER_SEPARATOR("&"),
    QUERY_KEY_VALUE_SEPARATOR("="),
    QUERY_DEFAULT_VALUE(" ");
    
    public final String value;

    ParsingConstant(String value) {
        this.value = value;
    }


}
