package http.servlet;

import java.io.IOException;
import java.nio.file.Paths;

import enums.ContentType;
import enums.HttpHeader;
import enums.HttpStatus;
import http.request.HttpRequest;
import http.response.HttpResponse;
import util.FileUtils;

/**
 * The type Static resource servlet.
 */
public class StaticResourceServlet implements Servlet {

	private static final String STATIC_FILES_PATH = "static";
	private static final String DEFAULT_HTML_FILE = "/index.html";

	@Override
	public void service(HttpRequest request, HttpResponse response) throws IOException {
		handleStaticResource(request, response);
	}

	/**
	 * Handle static resource.
	 *
	 * @param request the request
	 * @param response the response
	 * @throws IOException the io exception
	 */
	public static void handleStaticResource(HttpRequest request, HttpResponse response) throws IOException {
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
