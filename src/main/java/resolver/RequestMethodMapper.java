package resolver;

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
        ResourceResolver resolver = resolvers.get(request.getUri());
        if (resolver == null) {
            throw new IllegalArgumentException("Unknown resource: " + request.getUri());
        }
        resolver.resolve(request, response);
    }
}
