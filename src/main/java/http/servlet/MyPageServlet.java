package http.servlet;

import java.io.IOException;
import java.nio.file.Paths;

import enums.ContentType;
import enums.HttpHeader;
import enums.HttpStatus;
import http.HttpSessionStorage;
import http.request.HttpRequest;
import http.response.HttpResponse;
import util.FileUtils;

public class MyPageServlet implements Servlet {
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
		if (sessionId == null || HttpSessionStorage.getSession(sessionId) == null) {
			throw new IllegalArgumentException("로그인이 필요합니다.");
		}

		ContentType contentType = request.inferContentType();

		response.setStatusCode(HttpStatus.OK);
		response.setVersion(request.getVersion());
		response.setHeader(HttpHeader.CONTENT_TYPE.getValue(), contentType.getMimeType());
		response.setBody(body.getBytes());

	}
}
