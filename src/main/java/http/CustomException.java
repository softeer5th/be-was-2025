package http;

import enums.HttpStatus;

public class CustomException {

	private final HttpStatus status;
	private final String message;

	public CustomException(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}
}
