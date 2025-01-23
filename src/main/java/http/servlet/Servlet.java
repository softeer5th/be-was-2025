package http.servlet;

import java.io.IOException;

import http.request.HttpRequest;
import http.response.HttpResponse;

/**
 * The interface Servlet.
 */
public interface Servlet {
	/**
	 * Service.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws IOException the io exception
	 */
	void service(HttpRequest request, HttpResponse response) throws IOException;
}
