package util;

public class CookieParser {
    public static String parseCookie(String cookie) {
        // SID=PpbZz; Path=/
        return cookie.split(";")[0].substring("SID=".length());
    }
}
