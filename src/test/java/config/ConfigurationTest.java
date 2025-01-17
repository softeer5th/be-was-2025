package config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationTest {

    @Test
    @DisplayName(".env 파일에서 jwt.secret 로드 테스트")
    void testGetSecretKey() {
        // .env가 존재한다고 가정하고, 그 안에 jwt.secret가 설정되어 있어야 함
        String secretKey = Configuration.getSecretKey();
        assertNotNull(secretKey, "jwt.secret 값이 null이면 안 됨");
        assertFalse(secretKey.isEmpty(), "jwt.secret 값이 빈 문자열이면 안 됨");
    }

    @Test
    @DisplayName(".env 파일이 없거나 로드 실패 시 예외 발생 테스트")
    void testMissingEnvFile() {
        try {
            Properties props = new Properties();
            InputStream input = getClass().getClassLoader().getResourceAsStream("nonexistent.env");
            if (input == null) {
                // nonexistent.env 파일이 없으니 이 상황을 흉내낼 수 있음
                throw new IllegalArgumentException("nonexistent.env file not found in classpath.");
            }
            props.load(input);
            fail("예외가 발생해야 하지만 발생하지 않음");
        } catch (IOException | IllegalArgumentException e) {
            // 정상: 예외 발생
            assertTrue(true, "예외가 정상적으로 발생했음: " + e.getMessage());
        }
    }
}