package dto;

import http.request.HttpRequest;
import model.User;

/**
 * The type User request dto.
 */
public class UserRequestDto {
	private final String userId;
	private final String password;
	private final String name;
	private final String email;
	private final byte[] image;

	/**
	 * Instantiates a new User request dto.
	 *
	 * @param userId the user id
	 * @param password the password
	 * @param name the name
	 * @param email the email
	 * @param image the image
	 */
	public UserRequestDto(String userId, String password, String name, String email, byte[] image) {
		this.userId = userId;
		this.password = password;
		this.name = name;
		this.email = email;
		this.image = image;
	}

	/**
	 * Is valid boolean.
	 *
	 * @return the boolean
	 */
	public boolean isValid() {
		return userId != null && !userId.isEmpty()
			&& password != null && !password.isEmpty()
			&& name != null && !name.isEmpty()
			&& email != null && !email.isEmpty();
	}

	/**
	 * To user user.
	 *
	 * @return the user
	 */
	public User toUser() {
		return new User(userId, password, name, email, image);
	}
}

