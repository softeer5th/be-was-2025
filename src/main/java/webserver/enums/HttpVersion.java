package webserver.enums;


import java.util.Arrays;

public enum HttpVersion {
    HTTP_0_9("HTTP/0.9"),
    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2_0("HTTP/2.0"),
    HTTP_3_0("HTTP/3.0");

    public final String version;

    HttpVersion(String version) {
        this.version = version;
    }

    public static HttpVersion of(String version) {
        return Arrays.stream(values())
                .filter(httpVersion -> httpVersion.version.equalsIgnoreCase(version))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid HTTP version"));
    }
}
