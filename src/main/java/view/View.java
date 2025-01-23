package view;

import java.util.Map;
import java.util.Optional;

/**
 * The type View.
 */
public class View {
	private Map<String, Object> model;
	private String templateName;

	/**
	 * Instantiates a new View.
	 *
	 * @param model the model
	 * @param templateName the template name
	 */
	public View(Map<String, Object> model, String templateName) {
		this.model = model;
		this.templateName = templateName;
	}

	/**
	 * Gets attribute.
	 *
	 * @param name the name
	 * @return the attribute
	 */
	public Optional<Object> getAttribute(String name) {
		return Optional.ofNullable(model.get(name));
	}
}
