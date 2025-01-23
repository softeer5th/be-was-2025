package webserver;

import response.HTTPResponse;
import request.HTTPRequest;

public interface URIHandler {

    boolean supports(HTTPRequest request);
    HTTPResponse handle(HTTPRequest request);

}
