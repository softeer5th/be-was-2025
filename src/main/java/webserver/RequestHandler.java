package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import http.request.HttpRequest;
import http.response.HttpResponse;
import http.servlet.FrontControllerServlet;

public class RequestHandler implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
	private final Socket connection;

	public RequestHandler(final Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
			connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			handleRequest(in, out);
		} catch (IOException e) {
			logger.error("Error handling client connection: {}", e.getMessage());
		}
	}

	private void handleRequest(final InputStream in, final OutputStream out) throws IOException {

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in));
			DataOutputStream dos = new DataOutputStream(out)) {
			HttpRequest request = new HttpRequest(reader);
			HttpResponse response = new HttpResponse();

			FrontControllerServlet frontControllerServlet = FrontControllerServlet.getInstance();
			frontControllerServlet.service(request, response);

			response.sendResponse(dos);

		} catch (IOException e) {
			// TODO: 예외 처리 필요.

			logger.error("Failed to parse the request: {}", e.getMessage());
		}
	}
}
