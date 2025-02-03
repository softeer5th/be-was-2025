package webserver.functional;

import webserver.exception.HTTPException;
import webserver.message.record.FileRecord;

import java.util.HashMap;
import java.util.Map;

public class TypeParserFactory {
    private static final Map<Class<?>, TypeParser> typeParsers = new HashMap<>() {{
        put(String.class, (value -> (String)value));
        put(Integer.class, (value -> Integer.parseInt((String) value)));
        put(Double.class, (value -> Double.parseDouble((String) value)));
        put(Long.class, (value -> Long.parseLong((String)value)));
        put(Boolean.class,(value -> Boolean.parseBoolean((String) value)));
        put(Float.class, ((value) -> Float.parseFloat((String)value)));
        put(Byte.class, ((v) -> Byte.parseByte((String)v)));
        put(Short.class, ((v) -> Short.parseShort((String)v)));

        put(int.class, (value -> Integer.parseInt((String) value)));
        put(double.class, (value -> Double.parseDouble((String) value)));
        put(long.class,  (value -> Long.parseLong((String)value)));
        put(boolean.class, (value -> Boolean.parseBoolean((String) value)));
        put(float.class, ((value) -> Float.parseFloat((String)value)));
        put(byte.class, ((v) -> Byte.parseByte((String)v)));
        put(short.class, ((v) -> Short.parseShort((String)v)));

        put(FileRecord.class, (value -> (FileRecord)value));
    }};

    public static TypeParser getTypeParser(Class<?> targetType) {
        TypeParser parser = typeParsers.get(targetType);
        if (parser == null) {
            throw new IllegalStateException("Unsupported type: " + targetType);
        }
        return parser;
    }
}
