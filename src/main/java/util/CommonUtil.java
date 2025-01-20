package util;

public class CommonUtil {
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
}
