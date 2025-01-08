package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import enums.ContentType;
import enums.HttpHeader;
import enums.HttpMethod;
import http.request.HttpRequest;
import http.response.HttpResponse;
import enums.HttpStatus;

public class RequestHandler implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
	private static final String STATIC_FILES_PATH = "src/main/resources/static";

	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
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

	private void handleRequest(InputStream in, OutputStream out) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);

		try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
			HttpRequest request = new HttpRequest(reader);

			if (HttpMethod.GET.equals(request.getMethod())) {
				serveStaticFile(request, dos);
			} else {
				// response.send404(dos);
			}
		} catch (IOException e) {
			logger.error("Failed to parse the request: {}", e.getMessage());
			// response.send404();
		}
	}

	private void serveStaticFile(HttpRequest request, DataOutputStream dos) throws IOException {
		HttpResponse response = new HttpResponse();

		File file = new File(STATIC_FILES_PATH + request.getPath());
		if (file.exists() && file.isFile()) {
			byte[] body = Files.readAllBytes(file.toPath());
			ContentType contentType = request.inferContentType();

			response.setStatusCode(HttpStatus.OK);
			response.setVersion(request.getVersion());
			response.setHeader(HttpHeader.CONTENT_TYPE.getValue(), contentType.getMimeType());
			response.setBody(body);

			response.sendResponse(dos);
		} else {
			//response.send404();
		}
	}
}
