package http.servlet;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import db.ArticleDatabase;
import enums.ContentType;
import enums.HttpHeader;
import enums.HttpMethod;
import enums.HttpStatus;
import http.HttpSession;
import http.HttpSessionStorage;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.Article;
import model.User;
import util.FileUtils;

/**
 * The type Article servlet.
 */
public class ArticleServlet implements Servlet {
	private static final String STATIC_FILES_PATH = "static";
	private static final String DEFAULT_HTML_FILE = "/index.html";
	private static final String LOGIN_PAGE = "/login/index.html";

	private final ArticleDatabase articleDatabase;

	/**
	 * Instantiates a new Article servlet.
	 *
	 * @param articleDatabase the article database
	 */
	public ArticleServlet(ArticleDatabase articleDatabase) {
		this.articleDatabase = articleDatabase;
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

		if (HttpSessionStorage.getSession(request.getSessionId()) == null) {
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

	private void doPost(HttpRequest request, HttpResponse response) {
		List<Map<String, Object>> body = request.getBodyAsMultipart();
		if (body.isEmpty()) {
			throw new IllegalArgumentException("입력값이 잘못되었습니다.");
		}

		HttpSession session = HttpSessionStorage.getSession(request.getSessionId());
		if(session == null){
			response.setRedirectResponse(response, request.getVersion(), HttpStatus.TEMPORARY_REDIRECT, LOGIN_PAGE);
			return;
		}

		User user = (User)session.getAttribute("user");

		Object o = body.get(0).get("body");
		String content = (String)o;
		byte[] image = (byte[]) body.get(1).get("body");
		Article article = new Article(content, user.getUserId(), image);
		articleDatabase.save(article);

		response.setStatusCode(HttpStatus.FOUND);
		response.setVersion(request.getVersion());
		response.setRedirectResponse(response, request.getVersion(), HttpStatus.FOUND, DEFAULT_HTML_FILE);

	}
}
