package webserver;

public enum HTTPVersion {
    HTTP_1_0("HTTP/1.0"), HTTP_1_1("HTTP/1.1"), HTTP_2("HTTP/2");

    private final String version;
    HTTPVersion(String version) {
        this.version = version;
    }

    public static boolean isValid(String version) {
        for (HTTPVersion v : HTTPVersion.values()) {
            if (v.version.equalsIgnoreCase(version)) {
                return true;
            }
        }
        return false;
    }
}
