package http.servlet;

import java.io.IOException;

import http.request.HttpRequest;
import http.response.HttpResponse;

public interface Servlet {
	void service(HttpRequest request, HttpResponse response) throws IOException;
}
