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
        HTTPException lastException = null;
        for (ResourceResolver resolver : resolvers) {
            try {
                resolver.resolve(request, response);
                return;
            } catch (HTTPException e) {
                lastException = e;
                if (e.getStatusCode() == HTTPStatusCode.NOT_FOUND ||
                    e.getStatusCode() == HTTPStatusCode.METHOD_NOT_ALLOWED) {
                    continue;
                }
                throw e;
            }
        }
        throw lastException;
    }
}
