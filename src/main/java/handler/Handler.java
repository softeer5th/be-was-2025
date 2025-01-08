package handler;

import response.HttpResponse;
import request.RequestInfo;

public interface Handler {
    HttpResponse handle(RequestInfo request);
}
