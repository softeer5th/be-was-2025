package http.servlet;

import static enums.ContentType.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import enums.HttpStatus;
import http.HttpSession;
import http.HttpSessionStorage;
import http.request.HttpRequest;
import http.response.HttpResponse;
import view.TemplateEngine;

public class FrontControllerServlet implements Servlet {

	// 싱글톤 보장해주기 위함.
	private static final FrontControllerServlet INSTANCE = new FrontControllerServlet();
	private static final String STATIC_RESOURCES = "/resources";
	private static final String ERROR_PAGE = "/error.html";
	private final Map<String, Servlet> controllerMap;

	private FrontControllerServlet() {
		controllerMap = new HashMap<>();

		// TODO: 해당 객체는 정적인 객체인데 서블릿를 추가해주는 위치가 바람직한가?
		controllerMap.put(STATIC_RESOURCES, new StaticResourceServlet());

		controllerMap.put("/", new HomeServlet());
		controllerMap.put("/registration", new StaticResourceServlet());
		controllerMap.put("/login", new LoginServlet());
		controllerMap.put("/logout", new LogoutServlet());
		controllerMap.put("/mypage", new MyPageServlet());
		controllerMap.put("/article", new ArticleServlet());
		controllerMap.put("/user/create", new RegisterServlet());
	}

	public static FrontControllerServlet getInstance() {
		return INSTANCE;
	}

	@Override
	public void service(HttpRequest request, HttpResponse response) throws IOException {
		try {
			// TODO: HTML이 아닌 경우에만, 정적 리소스 서블릿으로 처리하는게 맞는가?
			if (request.hasExtension() && !request.inferContentType().equals(TEXT_HTML)) {
				Servlet servlet = controllerMap.get(STATIC_RESOURCES);
				servlet.service(request, response);
				return;
			}

			// path 기반으로 서블릿을 찾아서 실행하기에 순수한 path 만 추출
			String pathWithoutFileName = request.getPathWithoutFileName();
			if (!controllerMap.containsKey(pathWithoutFileName)) {
				// TODO: 에러 페이지 이동
				return;
			}

			controllerMap.get(pathWithoutFileName).service(request, response);
		} catch (Exception e) {
			response.setRedirectResponse(response, request.getVersion(), HttpStatus.TEMPORARY_REDIRECT, ERROR_PAGE);
		} finally {
			// 렌더
			HttpSession session = HttpSessionStorage.getSession(request.getSessionId());

			TemplateEngine templateEngine = new TemplateEngine();
			templateEngine.render(response, session);
		}
	}
}
