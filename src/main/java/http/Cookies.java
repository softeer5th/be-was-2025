package http;

import static enums.HttpHeader.*;
import static http.request.HttpHeaders.*;

import java.util.ArrayList;
import java.util.List;

public class Cookies {
	public static final String COOKIE_DELIMITER = "=";
	public static final String COOKIE_SEPARATOR = ";";
	public static final String COOKIE_SPACE = " ";
	private List<Cookie> cookies = new ArrayList<>();

	public Cookies(String cookieLine) {
		if (cookieLine == null || cookieLine.isEmpty()) {
			return;
		}

		String[] cookieStrings = cookieLine.split(COOKIE_SEPARATOR);

		for (String cookie : cookieStrings) {
			// 공백 제거 및 key=value 분리
			String[] cookieKeyValue = cookie.trim().split(COOKIE_DELIMITER, 2);
			if (cookieKeyValue.length == 2) {
				setCookie(cookieKeyValue[0], cookieKeyValue[1]);
			}
		}
	}

	public Cookies() {
		this.cookies = new ArrayList<>();
	}

	public boolean hasCookie() {
		return !cookies.isEmpty();
	}

	public String getCookie(String name) {
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(name)) {
				return cookie.getValue();
			}
		}
		return null;
	}

	public void setCookie(String name, String value, String... options) {
		cookies.add(new Cookie(name, value, options));
	}

	public String toMessage() {
		StringBuilder message = new StringBuilder();

		message.append(SET_COOKIE.getValue() + HEADER_DELIMITER + COOKIE_SPACE);

		for (Cookie cookie : cookies) {
			message.append(cookie.toMessage());
		}

		return message.toString();
	}
}

