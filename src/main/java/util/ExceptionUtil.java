package util;

import java.util.Optional;

/**
 * 예외 처리를 위한 유틸리티 클래스
 */
public class ExceptionUtil {
    /**
     * Checked Exception을 Unchecked Exception으로 변환하는 메서드.
     * 사용 시 try-catch로 예외를 처리할 필요가 없어진다.
     *
     * @param supplier Checked Exception을 발생시키는 함수형 인터페이스
     * @param <T>      supplier의 반환 타입
     * @return supplier의 실행 결과
     */
    public static <T> T wrapCheckedException(CheckedSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 예외 발생 시 Optional.empty()를 반환하는 메서드
     * 사용 시 try-catch로 예외를 처리할 필요가 없어진다.
     *
     * @param supplier Checked Exception을 발생시키는 함수형 인터페이스
     * @param <R>      supplier의 반환 타입
     * @return supplier의 실행 결과를 Optional로 감싼 값 . 예외가 발생하면 Optional.empty()를 반환한다.
     */
    // 예외가 발생하면 Optional.empty()를 반환하는 메서드
    public static <R> Optional<R> ignoreException(ExceptionUtil.CheckedSupplier<R> supplier) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    /**
     * Checked Exception을 던지는 Supplier 함수형 인터페이스
     *
     * @param <T> supplier의 반환 타입
     */
    public interface CheckedSupplier<T> {
        T get() throws Exception;
    }

    /**
     * Checked Exception을 던지는 Function 함수형 인터페이스
     *
     * @param <T> 함수의 입력 타입
     * @param <R> 함수의 반환 타입
     */
    public interface CheckedFunction<T, R> {
        R apply(T t) throws Exception;
    }

    /**
     * Checked Exception을 던지는 Consumer 함수형 인터페이스
     *
     * @param <T> 함수의 입력 타입
     */
    public interface CheckedConsumer<T> {
        void accept(T t) throws Exception;
    }
}
