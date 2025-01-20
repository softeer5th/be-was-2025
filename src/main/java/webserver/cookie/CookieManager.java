package webserver.cookie;

import util.enums.CookieName;

public class CookieManager {

    public static class SetCookie {
        private final StringBuilder cookieBuilder;

        public SetCookie(String name, String value) {
            this.cookieBuilder = new StringBuilder();
            this.cookieBuilder.append(name).append("=").append(value);
        }

        public SetCookie path(String path) {
            if (path != null && !path.isBlank()) {
                cookieBuilder.append("; Path=").append(path);
            }
            return this;
        }

        public SetCookie httpOnly() {
            cookieBuilder.append("; HttpOnly");
            return this;
        }

        public SetCookie secure() {
            cookieBuilder.append("; Secure");
            return this;
        }

        public SetCookie maxAge(int seconds) {
            if (seconds > 0) {
                cookieBuilder.append("; Max-Age=").append(seconds);
            }
            return this;
        }

        public String build() {
            return cookieBuilder.toString();
        }
    }

    public static String deleteCookie(CookieName cookieName) {
        return new SetCookie(cookieName.getName(), "").path("/").maxAge(0).build();
    }
}