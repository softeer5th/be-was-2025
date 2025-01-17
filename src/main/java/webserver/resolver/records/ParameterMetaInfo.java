package webserver.resolver.records;

import webserver.functional.TypeParser;
import webserver.message.HTTPRequest;

import java.util.Optional;
import java.util.function.BiFunction;

public record ParameterMetaInfo(
        String name,
        boolean required,
        TypeParser parser,
        BiFunction<HTTPRequest, String, Optional<String>> finder
) {
    private static BiFunction<HTTPRequest, String, Optional<String>> paramFinder = (req, name) -> {
        return req.getParameter(name, String.class);
    };

    private static BiFunction<HTTPRequest, String, Optional<String>> bodyFinder = (req, name) -> {
        return req.getBody(name, String.class);
    };

    public static ParameterMetaInfo forBody(String name, boolean required, TypeParser parser) {
        return new ParameterMetaInfo(name, required, parser, bodyFinder);
    }

    public static ParameterMetaInfo forParam(String name, boolean required, TypeParser parser) {
        return new ParameterMetaInfo(name, required, parser, paramFinder);
    }
}
