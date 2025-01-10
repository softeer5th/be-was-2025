package webserver.functional;

@FunctionalInterface
public interface TypeParser {
    Object parse(String value);
}
