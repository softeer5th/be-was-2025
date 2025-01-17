package util;

import config.Configuration;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    @Test
    @DisplayName("정상적인 User 객체로 JWT 토큰 생성 테스트")
    void testGenerateTokenWithValidUser() {
        User user = new User("testUser", "password123", "Test User", "test@example.com");
        String token = JwtUtil.generateToken(user);

        assertNotNull(token, "JWT 토큰이 정상적으로 생성되어야 함");
        assertFalse(token.isEmpty(), "JWT 토큰이 빈 문자열이면 안 됨");

        String secretKey = Configuration.getSecretKey();
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        var jwt = Jwts.parser()
                .verifyWith(key)          // jjwt 0.11.5+ 방식
                .build()
                .parseSignedClaims(token);

        var payload = jwt.getPayload();
        assertEquals("testUser", payload.getSubject(), "JWT subject는 userId와 일치해야 함");
        assertEquals("Test User", payload.get("name"), "name 클레임이 Test User와 같아야 함");
    }

    @Test
    @DisplayName("User ID가 null인 경우 토큰 생성 실패 테스트")
    void testGenerateTokenWithNullUserId() {
        User user = new User(null, "NoId", "pw", "noid@example.com");
        String token = JwtUtil.generateToken(user);
        assertNull(token, "userId가 null이면 토큰 생성이 안 되어야 함");
    }

    @Test
    @DisplayName("User 객체 자체가 null인 경우 처리 테스트")
    void testGenerateTokenWithNullUser() {
        // user가 null → generateToken 내부 user.getUserId() 접근 시 NPE 가능
        assertThrows(NullPointerException.class, () -> {
            JwtUtil.generateToken(null);
        }, "User 객체가 null이면 NPE 또는 안전 처리 필요");
    }

    @Test
    @DisplayName("Token 만료시간이 현재 시점보다 이후임을 확인 테스트")
    void testTokenExpiration() {
        User user = new User("expireTest", "Expire Test", "pw", "expire@example.com");
        String token = JwtUtil.generateToken(user);

        assertNotNull(token, "토큰 생성이 정상이어야 함");

        String secretKey = Configuration.getSecretKey();
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        var jwt = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);

        Date exp = jwt.getPayload().getExpiration();
        assertTrue(exp.after(new Date()), "토큰 만료시간은 현재시간 이후여야 함");
    }
}