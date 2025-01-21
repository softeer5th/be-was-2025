package handler.dynamic_handler;

import http.request.HttpRequest;
import http.response.HttpResponse;

public interface DynamicHtmlHandler {
    HttpResponse handle(byte[] fileData, String extension, HttpRequest httpRequest);
}
