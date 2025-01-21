package util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static util.ExceptionUtil.ignoreException;

// 리플렉션 유틸 클래스
public class ReflectionUtil {
    private static final String GET_METHOD = "get";
    private static final List<String> getterPrefix = List.of("get", "is");

    /* 객체의 필드를 가져오는 메서드. 만약 fieldName이 email이라면
       2. getEmail()
       3. isEmail()
       4. email()
       5. get("email")
       5. email 필드에 직접 접근
        순서대로 필드를 가져온다.
     */
    public static Optional<Object> callGetter(Object object, String fieldName) {
        if (object == null || fieldName == null || fieldName.isBlank())
            return Optional.empty();

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

    // 객체와, 그 객체를 탐색할 수 있는 경로(예: session.user.name)를 받아서 재귀적으로 객체를 탐색하는 메서드
    public static Optional<Object> recursiveCallGetter(Object object, String objectTraversalPath) {
        if (object == null || objectTraversalPath == null || objectTraversalPath.isBlank())
            return Optional.empty();

        String[] fields = objectTraversalPath.split("\\.");
        Object currentObject = object;
        for (String field : fields) {
            Optional<Object> value = callGetter(currentObject, field);
            if (value.isEmpty())
                return Optional.empty();
            currentObject = value.get();
        }
        return Optional.of(currentObject);
    }


    private static String toCamelCase(String prefix, String fieldName) {
        return prefix.toLowerCase() + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }

 
}
