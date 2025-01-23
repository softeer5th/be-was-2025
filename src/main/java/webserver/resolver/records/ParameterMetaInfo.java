package webserver.resolver.records;

import webserver.functional.TypeParser;
import webserver.message.HTTPRequest;

import java.util.Optional;
import java.util.function.BiFunction;

public record ParameterMetaInfo(
        String name,
        boolean required,
        boolean authenticated,
        TypeParser parser,
        BiFunction<HTTPRequest, String, Optional<Object>> finder
) {
    private static final BiFunction<HTTPRequest, String, Optional<Object>> paramFinder = (req, name) -> {
        return req.getParameter(name, Object.class);
    };

    private static final BiFunction<HTTPRequest, String, Optional<Object>> bodyFinder = (req, name) -> {
        return req.getBody(name, Object.class);
    };
    private static final BiFunction<HTTPRequest, String, Optional<String>> cookieFinder = HTTPRequest::getCookie;

    private static final BiFunction<HTTPRequest, String, Optional<Object>> cookieWrapper = (req, name) -> {
      Optional<String> found = cookieFinder.apply(req, name);
      if (found.isEmpty()) return Optional.empty();
      return Optional.of(found);
    };

    public static ParameterMetaInfo forBody(String name, boolean required, TypeParser parser) {
        return new ParameterMetaInfo(name, required, false, parser, bodyFinder);
    }

    public static ParameterMetaInfo forParam(String name, boolean required, TypeParser parser) {
        return new ParameterMetaInfo(name, required, false, parser, paramFinder);
    }

    public static ParameterMetaInfo forCookie(String name, boolean required, boolean authenticated, TypeParser parser) {
        return new ParameterMetaInfo(name, required, authenticated, parser, cookieWrapper);
    }
}
