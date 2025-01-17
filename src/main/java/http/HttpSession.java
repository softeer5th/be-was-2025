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

	public Object getAttribute(String name) {
		return values.get(name);
	}
}
