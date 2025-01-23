package http;

import static http.Cookies.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import enums.CookieType;

/**
 * The type Cookie.
 */
public class Cookie {
	private String name;
	private String value;
	private Map<CookieType, String> attributeValues;

	/**
	 * Instantiates a new Cookie.
	 *
	 * @param name the name
	 * @param value the value
	 * @param options the options
	 */
	public Cookie(String name, String value, String... options) {
		this.name = name;
		this.value = value;

		attributeValues = new HashMap<>();

		if(options.length % 2 != 0){
			throw new IllegalArgumentException("Invalid options");
		}

		for(int i=0; i<options.length; i+=2){
			attributeValues.put(CookieType.valueOf(options[i]), options[i+1]);
		}
	}

	/**
	 * Gets name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets value.
	 *
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * To message string.
	 *
	 * @return the string
	 */
	public String toMessage() {
		StringBuilder result = new StringBuilder(name + COOKIE_DELIMITER + value);

		// 속성 값 생성
		String attributes = attributeValues.entrySet().stream()
			.map(entry -> {
				if (entry.getValue().isEmpty()) {
					// 값이 필요 없는 속성 (예: HttpOnly, Secure)
					return entry.getKey().toString();
				}
				// 값이 있는 속성 (예: Max-Age=3600)
				return entry.getKey().getValue() + COOKIE_DELIMITER + entry.getValue();
			})
			.collect(Collectors.joining(COOKIE_SEPARATOR + COOKIE_SPACE));

		if (!attributes.isEmpty()) {
			result.append(COOKIE_SEPARATOR + COOKIE_SPACE).append(attributes);
		}

		return result.toString();
	}
}
