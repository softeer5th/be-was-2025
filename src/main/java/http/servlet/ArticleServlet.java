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

public class ArticleServlet implements Servlet {
	private static final String STATIC_FILES_PATH = "static";
	private static final String DEFAULT_HTML_FILE = "/index.html";
	private static final String LOGIN_PAGE = "/login/index.html";


	@Override
	public void service(HttpRequest request, HttpResponse response) throws IOException {
		handleStaticResource(request, response);
	}

	public static void handleStaticResource(HttpRequest request, HttpResponse response) throws IOException {

		if(HttpSessionStorage.getSession(request.getSessionId()) == null){
			response.setRedirectResponse(response, request.getVersion(), HttpStatus.TEMPORARY_REDIRECT, LOGIN_PAGE);
			return;
		}

		String path = Paths.get(STATIC_FILES_PATH, request.getPath()).toString();

		if (!request.hasExtension()) {
			path = Paths.get(path, DEFAULT_HTML_FILE).toString();
		}

		byte[] body = FileUtils.getFileAsByteArray(path);
		ContentType contentType = request.inferContentType();

		response.setStatusCode(HttpStatus.OK);
		response.setVersion(request.getVersion());
		response.setHeader(HttpHeader.CONTENT_TYPE.getValue(), contentType.getMimeType());
		response.setBody(body);
	}
}
