package http.servlet;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import db.Database;
import enums.HttpMethod;
import enums.HttpStatus;
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

		// HTTP 헤더의 쿠키 값을 SID=세션 ID 로 응답한다.
		// 세션 ID는 적당한 크기의 무작위 숫자 또는 문자열을 사용한다.
		// 서버는 세션 아이디에 해당하는 User 정보에 접근할 수 있어야 한다.

		response.setRedirectResponse(response, request.getVersion(), HttpStatus.TEMPORARY_REDIRECT, LOGIN_SUCCESS_PAGE);
	}
}
