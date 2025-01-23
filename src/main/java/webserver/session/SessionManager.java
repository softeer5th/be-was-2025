package webserver.session;

import java.util.Optional;

/**
 * 서버측에서 세션을 관리하는 역할
 */
public interface SessionManager {

    /**
     * 세션을 저장한다.
     *
     * @param session 저장할 세션
     */
    void saveSession(HttpSession session);

    /**
     * 세션을 조회한다.
     *
     * @param sessionId 조회할 세션 아이디
     * @return 조회된 세션. 세션이 없을 경우 Optional.empty()
     */
    Optional<HttpSession> getSession(String sessionId);

    /**
     * 세션을 삭제한다.
     *
     * @param sessionId 삭제할 세션 아이디
     */
    void removeSession(String sessionId);

    /**
     * 세션을 생성하고 저장한다.
     *
     * @return 생성된 세션
     */
    HttpSession createAndSaveSession();
}
