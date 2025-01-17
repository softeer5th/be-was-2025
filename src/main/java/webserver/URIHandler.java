package webserver;

import Response.HTTPResponse;
import request.HTTPRequest;

public interface URIHandler {

    boolean supports(HTTPRequest request);
    HTTPResponse handle(HTTPRequest request);

}
