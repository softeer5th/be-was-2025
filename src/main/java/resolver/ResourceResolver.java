package resolver;

import webserver.message.HTTPRequest;
import webserver.message.HTTPResponse;

public interface ResourceResolver {
    void resolve(HTTPRequest request, HTTPResponse.Builder response);
}
