package util.httprequest.handler;

import util.httprequest.HttpRequest;
import util.httprequest.HttpResponse;

import java.io.IOException;

public interface Handler {
    void handle(HttpRequest request, HttpResponse response) throws IOException;
}
