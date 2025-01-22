package webserver.session;

import java.util.Optional;

// 서버측에서 세션을 관리하는 역할
public interface SessionManager {

    void saveSession(HttpSession session);

    Optional<HttpSession> getSession(String sessionId);

    void removeSession(String sessionId);

    HttpSession createAndSaveSession();
}
