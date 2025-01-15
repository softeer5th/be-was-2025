package enums;

public enum CookieType {
	PATH("Path");

	private String value;
	CookieType(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
