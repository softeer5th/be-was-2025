package enums;

public enum HttpHeader {
	CONTENT_LENGTH("Content-Length"),
	ACCEPT("Accept"),
	LOCATION("Location"),
	COOKIE("Cookie"),
	SET_COOKIE("Set-Cookie"),
	CONTENT_TYPE("Content-Type");

	HttpHeader(String value) {
		this.value = value;
	}

	private final String value;

	public String getValue() {
		return value;
	}
}
