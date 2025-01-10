package dto;

import http.request.HttpRequest;
import model.User;

public class UserRequestDto {
	private final String userId;
	private final String password;
	private final String name;
	private final String email;

	public UserRequestDto(HttpRequest request) {
		this.userId = request.getParameter("userId");
		this.password = request.getParameter("password");
		this.name = request.getParameter("name");
		this.email = request.getParameter("email");
	}

	public boolean isValid() {
		return userId != null && !userId.isEmpty()
			&& password != null && !password.isEmpty()
			&& name != null && !name.isEmpty()
			&& email != null && !email.isEmpty();
	}

	public User toUser() {
		return new User(userId, password, name, email);
	}
}

