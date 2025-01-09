package enums;

public enum HttpMethod {
	GET, POST, PUT, PATCH, DELETE, HEAD;

	public static HttpMethod resolve(String method) {
		return switch (method) {
			case "GET" -> GET;
			case "HEAD" -> HEAD;
			case "POST" -> POST;
			case "PUT" -> PUT;
			case "PATCH" -> PATCH;
			case "DELETE" -> DELETE;
			default -> null;
		};
	}
}
