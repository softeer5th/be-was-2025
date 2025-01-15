package webserver.resolver;

import webserver.enumeration.HTTPStatusCode;
import webserver.exception.HTTPException;
import webserver.message.HTTPRequest;
import webserver.message.HTTPResponse;

public class SequentialResolver implements ResourceResolver {
    private final ResourceResolver [] resolvers;
    public SequentialResolver(ResourceResolver ... resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public void resolve(HTTPRequest request, HTTPResponse.Builder response) {
        Exception lastException = null;
        for (ResourceResolver resolver : resolvers) {
            try {
                resolver.resolve(request, response);
                return;
            } catch (Exception e) {
                lastException = e;
            }
        }
        throw new HTTPException.Builder()
                .causedBy(SequentialResolver.class)
                .notFound(request.getUri());
    }
}
