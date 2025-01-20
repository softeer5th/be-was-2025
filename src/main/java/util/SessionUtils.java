package util;

import db.SessionStore;
import http.HttpRequest;
import model.Session;
import util.exception.SessionNotFoundException;

public class SessionUtils {
    public static Session findSession(HttpRequest request) {
        if (!request.getSessionIds().containsKey(Cookie.SESSION_COOKIE_NAME)) {
            throw new SessionNotFoundException("쿠키에 세션이 존재하지 않습니다.");
        }
        String sessionId = request.getSessionIds().get(Cookie.SESSION_COOKIE_NAME);

        return SessionStore.findBySessionId(sessionId).orElseThrow(() ->
                new SessionNotFoundException("세션이 존재하지 않습니다."));
    }

    public static boolean isLogin(HttpRequest request) {
        if (!request.getSessionIds().containsKey(Cookie.SESSION_COOKIE_NAME)) {
            return false;
        }

        String sessionId = request.getSessionIds().get(Cookie.SESSION_COOKIE_NAME);
        if (SessionStore.findBySessionId(sessionId).isEmpty()) {
            return false;
        }

        return true;
    }
}
