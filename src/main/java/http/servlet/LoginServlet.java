package http.servlet;

import static http.HttpSessionStorage.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import db.UserDatabase;
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

	private final UserDatabase userDatabase;

	public LoginServlet(UserDatabase userDatabase) {
		this.userDatabase = userDatabase;
	}

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

		User foundUser = userDatabase.findUserByIdOrThrow(body.get().get("userId"));
		foundUser.validatePassword(body.get().get("password"));

		HashMap<String, Object> model = new HashMap<>();
		model.put("user", foundUser);

		String sessionId = UUID.randomUUID().toString().substring(0, 15);
		HttpSessionStorage.saveSession(new HttpSession(sessionId, model));

		response.setCookie(SESSION_ID, sessionId, CookieType.PATH.name(), "/");
		response.setRedirectResponse(response, request.getVersion(), HttpStatus.FOUND, LOGIN_SUCCESS_PAGE);
	}
}
