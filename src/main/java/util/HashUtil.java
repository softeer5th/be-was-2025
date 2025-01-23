package util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 해시 알고리즘을 사용하는 유틸리티 클래스
 */
public class HashUtil {
    private static final String HASH_ALGORITHM = "SHA-512";

    /**
     * 입력받은 문자열을 해시값으로 변환한다
     *
     * @param input 해시값으로 변환할 문자열
     * @return 해시값
     */
    public static String hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hash = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte ob : hash) {
                int i = (int) ob;
                sb.append(Integer.toString(i, 16));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("해시 알고리즘이 존재하지 않습니다.", e);
        }
    }
}
