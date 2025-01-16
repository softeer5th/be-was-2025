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
        User user = new User("testUser", "Test User", "password123", "test@example.com");
        String token = JwtUtil.generateToken(user);

        assertNotNull(token, "JWT 토큰이 정상적으로 생성되어야 함");
        assertFalse(token.isEmpty(), "JWT 토큰이 빈 문자열이면 안 됨");

        // 토큰 디코딩 검사 (간단 검증)
        String secretKey = Configuration.getSecretKey();
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        String subject = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();

        assertEquals("testUser", subject, "JWT 토큰의 subject는 userId와 일치해야 함");
    }

    @Test
    @DisplayName("User ID가 null인 경우 토큰 생성 실패 테스트")
    void testGenerateTokenWithNullUserId() {
        User user = new User(null, "No ID", "password", "noid@example.com");
        String token = JwtUtil.generateToken(user);

        assertNull(token, "userId가 null이면 토큰이 생성되지 않아야 함");
    }

    @Test
    @DisplayName("User 객체가 null인 경우 예외나 null 처리 테스트")
    void testGenerateTokenWithNullUser() {
        User user = null;
        // user가 null이므로 NullPointerException 등 발생 가능
        // 현재 코드 상에서는 user.getUserId() 접근 전 null 체크가 없음
        // 필요하다면 NullPointerException 발생을 검증하거나, safe-guard 로직 추가 가능

        assertThrows(NullPointerException.class, () -> JwtUtil.generateToken(user), "User가 null이면 NPE 발생 또는 적절히 처리되어야 함");
    }

    @Test
    @DisplayName("Token 유효시간 검사 (만료시간이 현재 시점 + 1시간인지 대략 검사)")
    void testTokenExpiration() {
        User user = new User("expireTest", "Expire Test", "pw", "expire@example.com");
        String token = JwtUtil.generateToken(user);

        assertNotNull(token, "토큰 생성은 성공해야 함");

        String secretKey = Configuration.getSecretKey();
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));

        var claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // 만료시간이 현재시간보다 이후인지 확인 (간단 검사)
        Date expiration = claims.getExpiration();
        assertTrue(expiration.after(new Date()), "토큰 만료시간이 현재 시점보다 이후여야 함");
    }
}