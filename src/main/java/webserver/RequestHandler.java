package webserver;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import http.request.HttpRequest;
import http.response.HttpResponse;
import http.servlet.FrontControllerServlet;

/**
 * The type Request handler.
 */
public class RequestHandler implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
	private final Socket connection;

	/**
	 * Instantiates a new Request handler.
	 *
	 * @param connectionSocket the connection socket
	 */
	public RequestHandler(final Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
			connection.getPort());

		try (InputStream in = new BufferedInputStream(connection.getInputStream());
			 OutputStream out = connection.getOutputStream()
		) {

			HttpRequest request = new HttpRequest(in);
			HttpResponse response = new HttpResponse();

			FrontControllerServlet frontControllerServlet = FrontControllerServlet.getInstance();
			frontControllerServlet.service(request, response);

			response.sendResponse(out);

		} catch (IOException e) {
			logger.error("Error handling client connection: {}", e);
		}
	}
}
