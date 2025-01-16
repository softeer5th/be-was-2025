package util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// 리플렉션 유틸 클래스
public class ReflectionUtil {
    private static final String GET_METHOD = "get";
    private static List<String> getterPrefix = List.of("get", "is");

    /* 객체의 필드를 가져오는 메서드. 만약 fieldName이 email이라면
       2. getEmail()
       3. isEmail()
       4. email()
       5. get("email")
       5. email 필드에 직접 접근
        순서대로 필드를 가져온다.
     */
    public static Optional<Object> getter(Object object, String fieldName) {
        if (object == null)
            return Optional.empty();
        if (fieldName.isBlank())
            throw new IllegalArgumentException("필드 이름이 비었습니다.");

        if (object instanceof Map<?, ?> m)
            return Optional.ofNullable(m.get(fieldName));
        if (object instanceof String) {
            return Optional.empty();
        }

        Class<?> clazz = object.getClass();

        List<String> candidateMethods = new ArrayList<>();
        for (String prefix : getterPrefix)
            candidateMethods.add(toCamelCase(prefix, fieldName));
        candidateMethods.add(fieldName);
        Optional<Object> result;
        for (String method : candidateMethods) {
            result = ignoreException(() -> clazz.getDeclaredMethod(method).invoke(object));
            if (result.isPresent())
                return result;

        }

        result = ignoreException(() -> clazz.getDeclaredMethod(GET_METHOD, String.class).invoke(object, fieldName));
        if (result.isPresent())
            return result;
        return ignoreException(() -> {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(object);
        });
    }

    // 예외가 발생하면 Optional.empty()를 반환하는 메서드
    private static <R> Optional<R> ignoreException(ThrowsSupplier<R> supplier) {
        try {
            return Optional.ofNullable(supplier.get());
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private static String toCamelCase(String prefix, String fieldName) {
        return prefix.toLowerCase() + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

    private interface ThrowsSupplier<T> {
        T get() throws Exception;
    }
}
