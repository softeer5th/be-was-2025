package http;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
	private final String id;
	private Map<String, Object> values = new HashMap<>();

	public HttpSession(String id, String key, Object value) {
		this.id = id;
		values.put(key, value);
	}

	public String getId() {
		return id;
	}

	public void setAttribute(String name, Object value) {
		values.put(name, value);
	}

	public void getAttribute(String name) {
		values.get(name);
	}

	public void removeAttribute(String name) {
		values.remove(name);
	}
}
