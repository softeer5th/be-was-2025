package http.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.UserDatabase;
import enums.ContentType;
import enums.HttpHeader;
import enums.HttpMethod;
import enums.HttpStatus;
import http.HttpSession;
import http.HttpSessionStorage;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;
import util.FileUtils;
import view.View;

public class MyPageServlet implements Servlet {
	private static final String STATIC_FILES_PATH = "static";
	private static final String DEFAULT_HTML_FILE = "/index.html";


	private final UserDatabase userDatabase;

	public MyPageServlet(UserDatabase userDatabase) {
		this.userDatabase = userDatabase;
	}

	@Override
	public void service(HttpRequest request, HttpResponse response) throws IOException {
		if (request.getMethod().equals(HttpMethod.GET)) {
			doGet(request, response);
		} else if (request.getMethod().equals(HttpMethod.POST)) {
			doPost(request, response);
		}
	}

	private void doGet(HttpRequest request, HttpResponse response) throws IOException {
		String path = Paths.get(STATIC_FILES_PATH, request.getPath()).toString();

		if (!request.hasExtension()) {
			path = Paths.get(path, DEFAULT_HTML_FILE).toString();
		}

		String body = FileUtils.getFileAsString(path);

		String sessionId = request.getSessionId();
		if (sessionId == null || HttpSessionStorage.getSession(sessionId) == null) {
			throw new IllegalArgumentException("로그인이 필요합니다.");
		}

		Map<String, Object> model = new HashMap<>();
		HttpSession session = HttpSessionStorage.getSession(sessionId);
		model.put("user", session.getAttribute("user"));

		ContentType contentType = request.inferContentType();

		response.setStatusCode(HttpStatus.OK);
		response.setVersion(request.getVersion());
		response.setHeader(HttpHeader.CONTENT_TYPE.getValue(), contentType.getMimeType());
		response.setBody(body.getBytes());
		response.setView(new View(model, path));
	}

	private void doPost(HttpRequest request, HttpResponse response) {
		String path = Paths.get(STATIC_FILES_PATH, request.getPath()).toString();

		if (!request.hasExtension()) {
			path = Paths.get(path, DEFAULT_HTML_FILE).toString();
		}

		List<Map<String, Object>> body = request.getBodyAsMultipart();

		String sessionId = request.getSessionId();
		if (sessionId == null || HttpSessionStorage.getSession(sessionId) == null) {
			throw new IllegalArgumentException("로그인이 필요합니다.");
		}

		HttpSession session = HttpSessionStorage.getSession(sessionId);
		User user = (User)session.getAttribute("user");
		byte[] image = (byte[]) body.get(0).get("body");

		user.updateUser((String)body.get(1).get("body"), image, (String)body.get(2).get("body"), (String)body.get(3).get("body"));

		userDatabase.updateUser(user);
		response.setRedirectResponse(response, request.getVersion(), HttpStatus.FOUND, DEFAULT_HTML_FILE);
	}

}
