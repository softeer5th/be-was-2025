package http;

import java.util.Map;

/**
 * The type Http session.
 */
public class HttpSession {
	private final String id;
	private Map<String, Object> values;

	/**
	 * Instantiates a new Http session.
	 *
	 * @param id the id
	 * @param values the values
	 */
	public HttpSession(String id, Map<String, Object> values) {
		this.id = id;
		this.values = values;
	}

	/**
	 * Gets id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets attribute.
	 *
	 * @param name the name
	 * @return the attribute
	 */
	public Object getAttribute(String name) {
		return values.getOrDefault(name, null);
	}

	/**
	 * Gets values.
	 *
	 * @return the values
	 */
	public Map<String, Object> getValues() {
		return values;
	}
}
