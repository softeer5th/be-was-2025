package webserver.session;

import java.util.Optional;

public interface SessionManager {

    void saveSession(String sessionId, HttpSession session);

    Optional<HttpSession> getSession(String sessionId);

    void removeSession(String sessionId);

    String createSessionId();
}
