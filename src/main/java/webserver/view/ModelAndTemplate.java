package webserver.view;

import java.util.HashMap;
import java.util.Map;

// template 이름과 렌더링할 데이터를 가지고 있는 클래스
public class ModelAndTemplate {
    private static final String ERROR_ATTRIBUTE_NAME = "error";
    private final String templateName;
    // template 을 렌더링할 때 필요한 데이터
    private final Map<String, Object> model;

    public ModelAndTemplate(String templateName) {
        this(templateName, new HashMap<>());
    }

    public ModelAndTemplate(String templateName, Map<String, Object> model) {
        this.templateName = templateName;
        this.model = model;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void addAttribute(String key, Object value) {
        model.put(key, value);
    }

    public void setError(String errorMessage) {
        model.put(ERROR_ATTRIBUTE_NAME, errorMessage);
    }

    public Map<String, Object> getModel() {
        return model;
    }
}
