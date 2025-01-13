package http.servlet;

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
	public static final String REGISTRATION_REGISTRATION_SUCCESS_HTML = "/registration/registration-success.html";

	@Override
	public void service(HttpRequest request, HttpResponse response) {
		if (request.getMethod().equals(HttpMethod.GET)) {
			UserRequestDto userRequest = new UserRequestDto(request);

			if (!userRequest.isValid()) {
				response.setStatusCode(HttpStatus.BAD_REQUEST);
				response.setVersion(request.getVersion());
				response.setBody("Invalid request parameters.".getBytes());
				logger.warn("Invalid user registration attempt: " + userRequest);
				return;
			}

			User user = userRequest.toUser();
			Optional<User> foundUser = Database.findUserById(user.getUserId());

			if(foundUser.isPresent()) {
				response.setStatusCode(HttpStatus.SEE_OTHER);
				response.setVersion(request.getVersion());

				// TODO: 이미 존재하는 리소스 페이지로 이동해야함.
				response.setRedirect(REGISTRATION_REGISTRATION_SUCCESS_HTML);

				logger.debug("User already exists: " + user);
				return;
			}

			Database.addUser(user);

			response.setStatusCode(HttpStatus.CREATED);
			response.setVersion(request.getVersion());
			response.setRedirect(REGISTRATION_REGISTRATION_SUCCESS_HTML);

			logger.debug("User: " + user + " is registered.");
		}
	}
}
