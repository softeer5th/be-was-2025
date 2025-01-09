package http.servlet;

import java.io.IOException;
import java.nio.file.Paths;

import enums.ContentType;
import enums.HttpHeader;
import enums.HttpStatus;
import http.request.HttpRequest;
import http.response.HttpResponse;
import util.FileUtils;

public class HomeServlet implements Servlet {
	private static final String STATIC_FILES_PATH = "static";
	private static final String DEFAULT_HTML_FILE = "/index.html";

	// TODO: StaticResourceServlet과 아예 똑같은 코드이다. 추후에 변동을 고려하여 분리하였다.
	@Override
	public void service(HttpRequest request, HttpResponse response) throws IOException {
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
