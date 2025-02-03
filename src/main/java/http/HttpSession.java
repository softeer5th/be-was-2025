package http;

import java.util.Map;

public class HttpSession {
	private final String id;
	private Map<String, Object> values;

	public HttpSession(String id, Map<String, Object> values) {
		this.id = id;
		this.values = values;
	}

	public String getId() {
		return id;
	}

	public Object getAttribute(String name) {
		return values.getOrDefault(name, null);
	}

	public Map<String, Object> getValues() {
		return values;
	}
}
