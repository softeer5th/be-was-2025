package webserver.view;

import java.util.Map;

public interface TemplateEngine {
    String render(String template, Map<String, Object> model);

    void registerTagHandler(TagHandler tagHandler);
}
