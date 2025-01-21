package util;

import java.util.Optional;

public class ExceptionUtil {

    public static <T> T wrapCheckedException(CheckedSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 예외가 발생하면 Optional.empty()를 반환하는 메서드
    public static <R> Optional<R> ignoreException(ExceptionUtil.CheckedSupplier<R> supplier) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    public interface CheckedSupplier<T> {
        T get() throws Exception;
    }

    public interface CheckedFunction<T, R> {
        R apply(T t) throws Exception;
    }
}
