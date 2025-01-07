package http;

public enum HttpStatus {

	OK(200, "OK"),
	NOT_FOUND(404, "Not Found");

	private final int value;
	private final String reasonPhrase;

	HttpStatus(int value, String reasonPhrase) {
		this.value = value;
		this.reasonPhrase = reasonPhrase;
	}

	public int getValue() {
		return value;
	}

	public String getReasonPhrase() {
		return reasonPhrase;
	}
}
