package webserver.functional;

@FunctionalInterface
public interface TypeParser {
    Object parse(Object value);
}
