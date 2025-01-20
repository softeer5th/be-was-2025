package handler;

import http.HttpRequest;
import http.HttpResponse;

import java.io.IOException;

public interface Handler {
   void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException;
}
