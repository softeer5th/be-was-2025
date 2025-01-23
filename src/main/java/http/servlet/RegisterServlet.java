package http.servlet;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.UserDatabase;
import dto.UserRequestDto;
import enums.HttpMethod;
import enums.HttpStatus;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;
import util.FileUtils;

public class RegisterServlet implements Servlet {
	private static final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);
	private static final String REGISTRATION_SUCCESS_PAGE = "/registration/registration-success.html";
	private static final String ERROR_PAGE = "/error.html";
	private static final String INVALID_REQUEST_MESSAGE = "Invalid request parameters.";
	private final UserDatabase userDatabase;

	public RegisterServlet(UserDatabase userDatabase) {
		this.userDatabase = userDatabase;
	}

	@Override
	public void service(HttpRequest request, HttpResponse response) throws IOException {
		if (request.getMethod().equals(HttpMethod.POST)) {
			doPost(request, response);
		} else {
			response.setStatusCode(HttpStatus.METHOD_NOT_ALLOWED);
			response.setVersion(request.getVersion());
			response.setBody("Method Not Allowed".getBytes());
		}
	}

	private void doPost(HttpRequest request, HttpResponse response) throws IOException {
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

		if (userDatabase.findUserById(user.getUserId()).isPresent()) {
			handleUserExists(response, request, user);
			return;
		}

		registerUser(response, request, user);
	}

	private void handleInvalidRequest(HttpResponse response, HttpRequest request) {
		response.setErrorResponse(response, request.getVersion(), HttpStatus.FOUND, INVALID_REQUEST_MESSAGE);

		logger.warn("Invalid user registration attempt: " + request);
	}

	private void handleUserExists(HttpResponse response, HttpRequest request, User user) {
		response.setRedirectResponse(response, request.getVersion(), HttpStatus.SEE_OTHER, ERROR_PAGE);
		logger.warn("User already exists: " + user);
	}

	private void registerUser(HttpResponse response, HttpRequest request, User user) {
		userDatabase.save(user);

		response.setRedirectResponse(response, request.getVersion(), HttpStatus.FOUND, REGISTRATION_SUCCESS_PAGE);
		logger.debug("User: " + user + " is registered.");
	}

	private UserRequestDto createUserRequestDto(Map<String, String> body) throws IOException {
		byte[] defaultImage = FileUtils.getFileAsByteArray("static/default.png");
		return new UserRequestDto(
			body.get("userId"),
			body.get("password"),
			body.get("name"),
			body.get("email"),
			defaultImage
		);
	}

}
