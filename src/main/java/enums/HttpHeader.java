package enums;

public enum HttpHeader {
	CONTENT_LENGTH("content-length"),
	ACCEPT("accept"),
	LOCATION("location"),
	COOKIE("cookie"),
	SET_COOKIE("set-cookie"),
	CONTENT_TYPE("content-type");

	HttpHeader(String value) {
		this.value = value;
	}

	private final String value;

	public String getValue() {
		return value;
	}
}
