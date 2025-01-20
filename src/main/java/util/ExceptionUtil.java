package util;

public class ExceptionUtil {

    public static <T> T wrapCheckedException(CheckedSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public interface CheckedSupplier<T> {
        T get() throws Exception;
    }
}
