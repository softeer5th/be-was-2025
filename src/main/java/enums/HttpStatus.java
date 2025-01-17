package enums;

public enum HttpStatus {

	OK(200, "OK"),
	CREATED(201, "Created"),
	FOUND(302, "Found"),
	SEE_OTHER(303, "See Other"),
	TEMPORARY_REDIRECT(307, "Temporary Redirect"),
	BAD_REQUEST(400, "Bad Request"),
	NOT_FOUND(404, "Not Found"),
	METHOD_NOT_ALLOWED(405, "Method Not Allowed");

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
