package http.servlet;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import db.Database;
import enums.CookieType;
import enums.HttpMethod;
import enums.HttpStatus;
import http.HttpSession;
import http.HttpSessionStorage;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

public class LoginServlet implements Servlet {

	private static final String LOGIN_SUCCESS_PAGE = "/index.html";
	private static final String LOGIN_FAILURE_PAGE = "/login/login_failed.html";

	@Override
	public void service(HttpRequest request, HttpResponse response) throws IOException {
		if (request.getMethod().equals(HttpMethod.GET)) {
			StaticResourceServlet.handleStaticResource(request, response);
		}
		else if(request.getMethod().equals(HttpMethod.POST)) {
			doPost(request, response);
		}
	}

	public void doPost(HttpRequest request, HttpResponse response){
		Optional<Map<String, String>> body = request.getBodyAsMap();

		if(body.isEmpty()){
			response.setRedirectResponse(response, request.getVersion(), HttpStatus.TEMPORARY_REDIRECT, LOGIN_FAILURE_PAGE);
			return;
		}

		User foundUser = Database.findUserByIdOrThrow(body.get().get("userId"));
		foundUser.validatePassword(body.get().get("password"));

		String sessionId = UUID.randomUUID().toString().substring(0, 15);
		HttpSessionStorage.createSession(new HttpSession(sessionId, "SID", foundUser));

		response.setCookie("SID", sessionId, CookieType.PATH.name(), "/");
		response.setRedirectResponse(response, request.getVersion(), HttpStatus.FOUND, LOGIN_SUCCESS_PAGE);
	}
}
