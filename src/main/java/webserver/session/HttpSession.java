package webserver.session;

import java.util.HashMap;
import java.util.Map;

// 세션 객체
public class HttpSession {
    private final String sessionId;
    private final Map<String, Object> sessionMap = new HashMap<>();

    // 세션의 상태. 이를 통해 세션이 저장되어야 하는지, 만료되어야 하는지 SessionInterceptor가 판단 가능
    private State state;

    public HttpSession(String sessionId) {
        this.sessionId = sessionId;
        state = State.NEW;
    }


    public void set(String key, Object value) {
        checkInvalidated();
        sessionMap.put(key, value);
    }

    public Object get(String key) {
        checkInvalidated();
        return sessionMap.get(key);
    }

    // 세션을 만료시키는 메서드
    public void invalidate() {
        this.state = State.INVALIDATED;
        this.sessionMap.clear();
    }

    @Override
    public String toString() {
        return "HttpSession{" + "session=" + sessionMap + '}';
    }

    public String getSessionId() {
        return sessionId;
    }

    // 세션을 활성화시키는 메서드. SessionInterceptor만 사용해야 하므로 외부에 노출시키지 않음
    void active() {
        this.state = State.ACTIVE;
    }

    // 세션의 상태를 나태내는 메서드. SessionInterceptor만 사용해야 하므로 외부에 노출시키지 않음
    State getState() {
        return state;
    }

    private void checkInvalidated() {
        if (state == State.INVALIDATED) {
            throw new IllegalStateException("Session is invalidated");
        }
    }

    enum State {
        // 새로 생성된 상태. 저장 필요
        NEW,
        // 활성화된 상태. 저장되어 있음
        ACTIVE,
        // 만료된 상태. 삭제 필요
        INVALIDATED
    }
}
