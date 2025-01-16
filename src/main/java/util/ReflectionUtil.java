package util;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// 리플렉션 유틸 클래스
public class ReflectionUtil {
    public static List<String> getterPrefix = List.of("get", "is");

    /* 객체의 필드를 가져오는 메서드. 만약 fieldName이 email이라면
       1. Map.get("email") (객체가 Map 인 경우)
       2. getEmail()
       3. isEmail()
       4. email()
       5. email 필드에 직접 접근
        순서대로 필드를 가져온다.
     */
    public static Optional<Object> getter(Object object, String fieldName) {
        if (fieldName.isBlank())
            throw new IllegalArgumentException("필드 이름이 비었습니다.");

        if (object instanceof Map) {
            return Optional.ofNullable(((Map<?, ?>) object).get(fieldName));
        }

        Class<?> clazz = object.getClass();
        for (String prefix : getterPrefix) {
            try {
                return Optional.ofNullable(clazz.getDeclaredMethod(toCamelCase(prefix, fieldName)).invoke(object));
            } catch (Exception ignored) {
            }
        }
        try {
            return Optional.ofNullable(clazz.getDeclaredMethod(fieldName).invoke(object));
        } catch (Exception ignored) {
        }
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            return Optional.ofNullable(field.get(object));
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return Optional.empty();
        }
    }
    
    private static String toCamelCase(String prefix, String fieldName) {
        return prefix.toLowerCase() + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }
}
