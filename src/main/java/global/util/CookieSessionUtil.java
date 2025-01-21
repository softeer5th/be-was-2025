package global.util;

import model.User;
import webserver.SessionManager;

import java.util.Map;

public class CookieSessionUtil {

    public static User getUserFromSession(Map<String, String> headers) {
        if (headers == null) return null;
        String cookieHeader = headers.get("Cookie");
        if (cookieHeader == null) return null;

        String sid = extractSid(cookieHeader);
        if (sid == null) return null;

        return SessionManager.getUser(sid);
    }

    public static String extractSid(String cookieHeader) {
        String[] cookiePairs = cookieHeader.split(";");
        for (String pair : cookiePairs) {
            String[] kv = pair.trim().split("=", 2);
            if (kv.length == 2 && "SID".equals(kv[0])) {
                return kv[1];
            }
        }
        return null;
    }
}