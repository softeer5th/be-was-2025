package http.enums;

import java.util.HashMap;
import java.util.Map;

public enum HttpVersion {
    HTTP_1_0("HTTP/1.0"),
    HTTP_1_1("HTTP/1.1"),
    HTTP_2_0("HTTP/2.0"),
    INVALID("INVALID");

    private final String version;

    private static final Map<String, HttpVersion> HTTP_VERSION_MAP = new HashMap();

    static {
        for (HttpVersion version : values()) {
            HTTP_VERSION_MAP.put(version.version, version);
        }
    }

    HttpVersion(String version) {
        this.version = version;
    }

    public String getVersion() {
        return this.version;
    }

    public static HttpVersion getVersionFromString(String version) {
        HttpVersion httpVersion = HTTP_VERSION_MAP.get(version);
        return httpVersion != null ? httpVersion : INVALID;
    }
}
