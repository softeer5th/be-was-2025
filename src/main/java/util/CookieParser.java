package util;

public class CookieParser {
    private static final String SESSION_COOKIE_PREFIX = "SID=";
    private static final String COOKIE_SEPARATOR = ";";
    public static String parseCookie(String cookie) {
        // 쿠키 값이 없을 경우 null을 반환한다.
        if (cookie == null) return null;
        // SID=PpbZz; Path=/
        return cookie.split(COOKIE_SEPARATOR)[0].substring(SESSION_COOKIE_PREFIX.length());
    }
}
