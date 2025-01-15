package webserver.functional;

import webserver.exception.HTTPException;

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

    public static TypeParser getTypeParser(Class<?> targetType) {
        TypeParser parser = typeParsers.get(targetType);
        if (parser == null) {
            throw new IllegalStateException("Unsupported type: " + targetType);
        }
        return parser;
    }
}
