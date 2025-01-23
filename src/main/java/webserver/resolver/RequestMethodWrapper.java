package webserver.resolver;

import webserver.exception.HTTPException;
import webserver.message.record.ResponseData;
import webserver.resolver.records.ParameterMetaInfo;
import webserver.enumeration.HTTPContentType;
import webserver.enumeration.HTTPStatusCode;
import webserver.message.HTTPRequest;
import webserver.message.HTTPResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
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
            Object result = method.invoke(handlerGroup, args);
            response.contentType(HTTPContentType.APPLICATION_JSON);
            response.statusCode(HTTPStatusCode.OK);
            if (result.getClass().isAssignableFrom(ResponseData.class)) {
                ResponseData unwrapped = (ResponseData) result;
                Map<String, String> headers = unwrapped.getHeaders();
                response.statusCode(unwrapped.getStatus());
                for (Map.Entry<String, String> header : headers.entrySet()) {
                    response.setHeader(header.getKey(), header.getValue());
                }
                response.contentType(unwrapped.getContentType());
                try (ByteArrayOutputStream bout = new ByteArrayOutputStream();) {
                    Object object = unwrapped.getData();
                    if (object instanceof String) {
                        bout.write(((String)object).getBytes());
                    }
                    response.body(bout.toByteArray());
                }
                response.setCookies(unwrapped.getSetCookies());
            }
        } catch (IllegalAccessException | InvocationTargetException | IOException e) {
            throw new HTTPException.Builder()
                    .causedBy("method invoke")
                    .internalServerError(e.getMessage());
        }
    }

    private Object[] getParameters(HTTPRequest request, HTTPResponse.Builder response) {
        Object[] args = new Object[parameters.length];
        int argIndex = 0;
        for (ParameterMetaInfo meta : parameters) {
            Optional<Object> parameter = meta.finder().apply(request, meta.name());
            if (meta.required() && parameter.isEmpty()) {
                throw new HTTPException.Builder()
                        .causedBy(method.getName())
                        .badRequest("Missing required parameter " + meta.name());
            }
            if (parameter.isPresent()) {
                try {
                    Object parsed = meta.parser().parse((String)parameter.get());
                    args[argIndex++] = parsed;
                } catch (NumberFormatException e) {
                    throw new HTTPException.Builder()
                            .causedBy("meta parser")
                            .badRequest("Invalid type parameter for " + meta.name());
                }
            }
        }
        return args;
    }
}
