package http;

import enums.HttpStatus;

/**
 * The type Custom exception.
 */
public class CustomException {

	private final HttpStatus status;
	private final String message;

	/**
	 * Instantiates a new Custom exception.
	 *
	 * @param status the status
	 * @param message the message
	 */
	public CustomException(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
	}
}
