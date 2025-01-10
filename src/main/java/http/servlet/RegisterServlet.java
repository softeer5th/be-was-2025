package http.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.Database;
import enums.HttpMethod;
import enums.HttpStatus;
import http.request.HttpRequest;
import http.response.HttpResponse;
import model.User;

public class RegisterServlet implements Servlet {
	private static final Logger logger = LoggerFactory.getLogger(RegisterServlet.class);

	@Override
	public void service(HttpRequest request, HttpResponse response) {
		if (request.getMethod().equals(HttpMethod.GET)) {

			User user = new User(request.getParameter("userId"), request.getParameter("password"),
					request.getParameter("name"), request.getParameter("email"));

			Database.addUser(user);

			response.setStatusCode(HttpStatus.FOUND);
			response.setVersion(request.getVersion());
			response.setRedirect("/registration/registration-success.html");
			logger.debug("User: " + user + " is registered.");
		}
	}
}
