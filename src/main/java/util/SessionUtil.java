package util;

import java.security.SecureRandom;

public class SessionUtil {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int SESSION_ID_LENGTH = 32; // 세션 ID 길이

    public static String generateSessionID() {
        SecureRandom random = new SecureRandom();
        StringBuilder sessionId = new StringBuilder(SESSION_ID_LENGTH);

        for (int i = 0; i < SESSION_ID_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length()); // 랜덤 인덱스 생성
            sessionId.append(CHARACTERS.charAt(randomIndex)); // 랜덤 문자 추가
        }

        return sessionId.toString();
    }
}
