package handler;

import http.HttpRequestInfo;
import http.HttpResponse;

public interface Handler {

    HttpResponse handle(HttpRequestInfo request);
}