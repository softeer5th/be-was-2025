package resolver;

import resolver.records.ParameterMetaInfo;
import webserver.enumeration.HTTPContentType;
import webserver.enumeration.HTTPStatusCode;
import webserver.message.HTTPRequest;
import webserver.message.HTTPResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

public class RequestMethodWrapper implements ResourceResolver {
    private ParameterMetaInfo[] parameters;
    private Method method;
    private Object handlerGroup;

    public RequestMethodWrapper(Object handlerGroup, Method method, ParameterMetaInfo[] parameters) {
        this.method = method;
        this.parameters = parameters;
        this.handlerGroup = handlerGroup;
    }

    @Override
    public void resolve(HTTPRequest request, HTTPResponse.Builder response) {
        Object[] args = getParameters(request, response);
        try {
            method.invoke(handlerGroup, args);
            response.body("SUCCESS".getBytes());
            response.contentType(HTTPContentType.APPLICATION_JSON);
            response.statusCode(HTTPStatusCode.OK);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    private Object[] getParameters(HTTPRequest request, HTTPResponse.Builder response) {
        Object[] args = new Object[parameters.length];
        int argIndex = 0;
        for (ParameterMetaInfo meta : parameters) {
            Optional<String> parameter = request.getParameter(meta.name(), String.class);
            if (meta.required() && parameter.isEmpty()) {
                throw new IllegalArgumentException("Missing required parameter " + meta.name());
            }
            if (parameter.isPresent()) {
                Object parsed = meta.parser().parse(parameter.get());
                System.out.printf("parse : %s\n", parsed);
                args[argIndex++] = parsed;
            }
        }
        System.out.println(argIndex);
        return args;
    }
}
