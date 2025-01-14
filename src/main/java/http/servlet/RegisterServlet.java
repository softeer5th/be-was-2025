package http.servlet;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.Database;
import dto.UserRequestDto;
import enums.HttpMethod;
import enums.HttpStatus;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

public class RegisterServlet implements Servlet {
	private static final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);
	private static final String REGISTRATION_SUCCESS_PAGE = "/registration/registration-success.html";
	private static final String INVALID_REQUEST_MESSAGE = "Invalid request parameters.";
	@Override
	public void service(HttpRequest request, HttpResponse response) {
		if (request.getMethod().equals(HttpMethod.POST)) {
			doPost(request, response);
		} else {
			response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
			response.setVersion(request.getVersion());
			response.setBody("Method Not Allowed".getBytes());
		}
	}

	private void doPost(HttpRequest request, HttpResponse response) {
		Optional<Map<String, String>> body = request.getBodyAsMap();

		if (body.isEmpty()) {
			handleInvalidRequest(response, request);
			return;
		}

		UserRequestDto userRequest = createUserRequestDto(body.get());
		if (!userRequest.isValid()) {
			handleInvalidRequest(response, request);
			return;
		}

		User user = userRequest.toUser();

		if (Database.findUserById(user.getUserId()).isPresent()) {
			handleUserExists(response, request, user);
		} else {
			registerUser(response, request, user);
		}
	}

	private void handleInvalidRequest(HttpResponse response, HttpRequest request) {
		sendErrorResponse(response, request.getVersion(), HttpStatus.BAD_REQUEST, INVALID_REQUEST_MESSAGE);
		logger.warn("Invalid user registration attempt: " + request);
	}

	private void handleUserExists(HttpResponse response, HttpRequest request, User user) {
		sendRedirectResponse(response, request.getVersion(), HttpStatus.SEE_OTHER, REGISTRATION_SUCCESS_PAGE);
		logger.debug("User already exists: " + user);
	}

	private void registerUser(HttpResponse response, HttpRequest request, User user) {
		Database.addUser(user);
		sendRedirectResponse(response, request.getVersion(), HttpStatus.FOUND, REGISTRATION_SUCCESS_PAGE);
		logger.debug("User: " + user + " is registered.");
	}

	private void sendErrorResponse(HttpResponse response, String version, HttpStatus status, String message) {
		response.setStatusCode(status);
		response.setVersion(version);
		response.setBody(message.getBytes());
	}

	private void sendRedirectResponse(HttpResponse response, String version, HttpStatus status, String location) {
		response.setStatusCode(status);
		response.setVersion(version);
		response.setRedirect(location);
	}

	private UserRequestDto createUserRequestDto(Map<String, String> body) {
		return new UserRequestDto(
			body.get("userId"),
			body.get("password"),
			body.get("name"),
			body.get("email")
		);
	}
}
