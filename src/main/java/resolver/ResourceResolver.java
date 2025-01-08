package resolver;

import webserver.message.HTTPRequest;

public interface ResourceResolver {
    byte [] resolve(HTTPRequest request);
}
