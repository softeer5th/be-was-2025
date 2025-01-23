package util;

/**
 * 공통으로 사용되는 유틸리티 클래스
 */
public class CommonUtil {
    /**
     * AutoCloseable 객체를 닫는다.
     *
     * @param autoCloseable 닫을 AutoCloseable 객체. 여러 개를 넘길 수 있다. null이면 무시한다.
     */
    public static void close(AutoCloseable... autoCloseable) {
        for (AutoCloseable closeable : autoCloseable) {
            if (closeable == null) return;
            try {
                closeable.close();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static boolean isBlank(String str) {
        return str == null || str.isBlank();
    }

    public static boolean hasLength(String str, int min, int max) {
        return str != null && str.length() >= min && str.length() <= max;
    }
}
