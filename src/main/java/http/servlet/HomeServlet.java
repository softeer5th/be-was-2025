package http.servlet;

import static http.HttpSessionStorage.*;

import java.io.IOException;
import java.nio.file.Paths;

import enums.ContentType;
import enums.HttpHeader;
import enums.HttpStatus;
import http.HttpSessionStorage;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;
import util.FileUtils;
import view.HomePageTemplate;

public class HomeServlet implements Servlet {
	private static final String STATIC_FILES_PATH = "static";
	private static final String DEFAULT_HTML_FILE = "/index.html";

	@Override
	public void service(HttpRequest request, HttpResponse response) throws IOException {
		String path = Paths.get(STATIC_FILES_PATH, request.getPath()).toString();

		if (!request.hasExtension()) {
			path = Paths.get(path, DEFAULT_HTML_FILE).toString();
		}

		String body = FileUtils.getFileAsString(path);

		String sessionId = request.getSessionId();
		if (HttpSessionStorage.getSession(sessionId) != null) {
			User foundUser = (User)getSession(sessionId).getAttribute(SESSION_ID);
			body = HomePageTemplate.renderLoginPage(body, foundUser);
		}

		ContentType contentType = request.inferContentType();

		response.setStatusCode(HttpStatus.OK);
		response.setVersion(request.getVersion());
		response.setHeader(HttpHeader.CONTENT_TYPE.getValue(), contentType.getMimeType());
		response.setBody(body.getBytes());
	}
}
