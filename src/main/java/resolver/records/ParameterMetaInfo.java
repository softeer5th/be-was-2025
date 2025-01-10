package resolver.records;

import webserver.functional.TypeParser;

public record ParameterMetaInfo(
        String name,
        boolean required,
        TypeParser parser
) {
}
