package view;

import java.util.Map;
import java.util.Optional;

public class View {
	private Map<String, Object> model;
	private String templateName;

	public View(Map<String, Object> model, String templateName) {
		this.model = model;
		this.templateName = templateName;
	}

	public Optional<Object> getAttribute(String name) {
		return Optional.ofNullable(model.get(name));
	}
}
