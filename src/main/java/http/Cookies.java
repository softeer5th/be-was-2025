package http;

import static enums.HttpHeader.*;
import static http.request.HttpHeaders.*;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Cookies.
 */
public class Cookies {
	/**
	 * The constant COOKIE_DELIMITER.
	 */
	public static final String COOKIE_DELIMITER = "=";
	/**
	 * The constant COOKIE_SEPARATOR.
	 */
	public static final String COOKIE_SEPARATOR = ";";
	/**
	 * The constant COOKIE_SPACE.
	 */
	public static final String COOKIE_SPACE = " ";
	private List<Cookie> cookies = new ArrayList<>();

	/**
	 * Instantiates a new Cookies.
	 *
	 * @param cookieLine the cookie line
	 */
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

	/**
	 * Instantiates a new Cookies.
	 */
	public Cookies() {
		this.cookies = new ArrayList<>();
	}

	/**
	 * Has cookie boolean.
	 *
	 * @return the boolean
	 */
	public boolean hasCookie() {
		return !cookies.isEmpty();
	}

	/**
	 * Gets cookie.
	 *
	 * @param name the name
	 * @return the cookie
	 */
	public String getCookie(String name) {
		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(name)) {
				return cookie.getValue();
			}
		}
		return null;
	}

	/**
	 * Sets cookie.
	 *
	 * @param name the name
	 * @param value the value
	 * @param options the options
	 */
	public void setCookie(String name, String value, String... options) {
		cookies.add(new Cookie(name, value, options));
	}

	/**
	 * To message string.
	 *
	 * @return the string
	 */
	public String toMessage() {
		StringBuilder message = new StringBuilder();

		message.append(SET_COOKIE.getValue() + HEADER_DELIMITER + COOKIE_SPACE);

		for (Cookie cookie : cookies) {
			message.append(cookie.toMessage());
		}

		return message.toString();
	}
}

