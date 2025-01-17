package db;

import model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SessionDBTest {

    @Test
    @DisplayName("세션 저장 후 조회 테스트")
    void testSaveAndGetSession() {
        String sessionId = "session-123";
        User user = new User("testUser", "Tester", "password", "test@example.com");

        SessionDB.saveSession(sessionId, user);

        User savedUser = SessionDB.getUser(sessionId);
        assertNotNull(savedUser, "세션에 저장한 사용자를 정상적으로 조회해야 함");
        assertEquals("testUser", savedUser.getUserId(), "조회된 User의 아이디가 일치해야 함");
    }

    @Test
    @DisplayName("세션 삭제 테스트")
    void testRemoveSession() {
        String sessionId = "session-456";
        User user = new User("removeUser", "Remove Tester", "password", "remove@example.com");

        SessionDB.saveSession(sessionId, user);
        User savedUser = SessionDB.getUser(sessionId);
        assertNotNull(savedUser, "삭제 전에는 사용자가 존재해야 함");

        SessionDB.removeSession(sessionId);
        User removedUser = SessionDB.getUser(sessionId);
        assertNull(removedUser, "삭제 후에는 사용자를 조회할 수 없어야 함");
    }

    @Test
    @DisplayName("존재하지 않는 세션 조회 시 null 반환 테스트")
    void testGetNonExistentSession() {
        User result = SessionDB.getUser("no-such-session");
        assertNull(result, "존재하지 않는 세션 ID를 조회하면 null 이어야 함");
    }
}