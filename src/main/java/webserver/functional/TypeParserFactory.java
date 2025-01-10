package webserver.functional;

import java.util.HashMap;
import java.util.Map;

public class TypeParserFactory {
    private static final Map<Class<?>, TypeParser> typeParsers = new HashMap<>() {{
        put(String.class, (value -> value));
        put(Integer.class, (Integer::parseInt));
        put(Double.class, (Double::parseDouble));
        put(Long.class, (Long::parseLong));
        put(Boolean.class, (Boolean::parseBoolean));
        put(Float.class, (Float::parseFloat));
        put(Byte.class, (Byte::parseByte));
        put(Short.class, (Short::parseShort));

        put(int.class, (Integer::parseInt));
        put(double.class, (Double::parseDouble));
        put(long.class, (Long::parseLong));
        put(boolean.class, (Boolean::parseBoolean));
        put(float.class, (Float::parseFloat));
        put(byte.class, (Byte::parseByte));
    }};
    static TypeParser defaultParser = (value) -> {
        throw new IllegalArgumentException("Not supported Type : " + value);
    };
    public static TypeParser getTypeParser(Class<?> targetType) {
        return typeParsers.getOrDefault(targetType, defaultParser);
    }
}
