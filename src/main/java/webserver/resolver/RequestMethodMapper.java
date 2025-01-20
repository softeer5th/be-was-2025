package webserver.resolver;

import webserver.enumeration.HTTPStatusCode;
import webserver.exception.HTTPException;
import webserver.message.HTTPRequest;
import webserver.message.HTTPResponse;

import java.util.Map;

public class RequestMethodMapper implements ResourceResolver{
    private final Map<String, ResourceResolver> resolvers;

    public RequestMethodMapper(Map<String, ResourceResolver> resolvers) {
        this.resolvers = resolvers;
    }
    @Override
    public void resolve(HTTPRequest request, HTTPResponse.Builder response) {
        ResourceResolver resolver = resolvers.get(request.getMethod() + " " + request.getUri());
        if (resolver == null) {
            throw new HTTPException.Builder()
                    .causedBy(RequestMethodWrapper.class)
                    .statusCode(HTTPStatusCode.METHOD_NOT_ALLOWED)
                    .build();
        }
        resolver.resolve(request, response);
    }
}
