package http.servlet;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import db.ArticleDatabase;
import db.UserDatabase;
import dto.Cursor;
import enums.ContentType;
import enums.HttpHeader;
import enums.HttpStatus;
import http.HttpSession;
import http.HttpSessionStorage;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.Article;
import model.User;
import util.FileUtils;
import view.View;

/**
 * The type Home servlet.
 */
public class HomeServlet implements Servlet {
	private static final String STATIC_FILES_PATH = "static";
	private static final String DEFAULT_HTML_FILE = "/index.html";
	private final ArticleDatabase articleDatabase;
	private final UserDatabase userDatabase;

	/**
	 * Instantiates a new Home servlet.
	 *
	 * @param articleDatabase the article database
	 * @param userDatabase the user database
	 */
	public HomeServlet(ArticleDatabase articleDatabase, UserDatabase userDatabase) {
		this.articleDatabase = articleDatabase;
		this.userDatabase = userDatabase;
	}

	@Override
	public void service(HttpRequest request, HttpResponse response) throws IOException {
		String path = Paths.get(STATIC_FILES_PATH, request.getPath()).toString();

		if (!request.hasExtension()) {
			path = Paths.get(path, DEFAULT_HTML_FILE).toString();
		}

		Map<String, Object> model = new HashMap<>();

		HttpSession session = HttpSessionStorage.getSession(request.getSessionId());
		if (session != null) {
			model.put("user", session.getAttribute("user"));
		}

		String page = request.getParameter("page");
		if(page == null || page.isEmpty()){
			page = "1";
		}

		Cursor<Article> foundArticle = articleDatabase.findNthArticle(Integer.parseInt(page));
		foundArticle.getContent().ifPresent(article -> {
			model.put("article", article);
			model.put("hasPrevPage", foundArticle.hasPrevPage());
			model.put("hasNextPage", foundArticle.hasNextPage());
			model.put("prevPageNumber", foundArticle.getPrevPageNumber());
			model.put("nextPageNumber", foundArticle.getNextPageNumber());
			User articleUser = userDatabase.findUserByIdOrThrow(article.getUserId());
			model.put("article_user", articleUser);
		});

		String body = FileUtils.getFileAsString(path);
		ContentType contentType = request.inferContentType();

		response.setStatusCode(HttpStatus.OK);
		response.setVersion(request.getVersion());
		response.setHeader(HttpHeader.CONTENT_TYPE.getValue(), contentType.getMimeType());
		response.setBody(body.getBytes());
		response.setView(new View(model, path));
	}
}
