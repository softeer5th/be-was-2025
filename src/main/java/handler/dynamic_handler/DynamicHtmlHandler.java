package handler.dynamic_handler;

import http.request.HttpRequest;

public interface DynamicHtmlHandler {
    byte[] handle(byte[] fileData, HttpRequest httpRequest);
}
