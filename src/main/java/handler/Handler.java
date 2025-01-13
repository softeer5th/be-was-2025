package handler;

import response.HttpResponse;
import request.HttpRequestInfo;

public interface Handler {
    HttpResponse handle(HttpRequestInfo request);
}
