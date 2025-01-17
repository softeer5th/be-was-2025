package http.servlet;

import enums.HttpStatus;
import http.HttpSessionStorage;
import http.request.HttpRequest;
import http.response.HttpResponse;

public class LogoutServlet implements Servlet {

	private static final String LOGOUT_SUCCESS_PAGE = "/index.html";

	@Override
	public void service(HttpRequest request, HttpResponse response) {
		// 세션 초기화
		String sessionId = request.getSessionId();
		if (sessionId != null) {
			HttpSessionStorage.removeSession(sessionId);
		}

		response.setRedirectResponse(response, request.getVersion(), HttpStatus.FOUND, LOGOUT_SUCCESS_PAGE);
	}
}
