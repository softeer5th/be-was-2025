package webserver.view.tag;

import webserver.view.TagHandler;
import webserver.view.TemplateFileReader;

import java.util.Map;

public class IncludeTagHandler extends TagHandler {
    public static final String DEFAULT_TAG_NAME = "include";
    public static final String TEMPLATE_ATTRIBUTE_NAME = "template";
    private final TemplateFileReader fileReader;

    public IncludeTagHandler(TemplateFileReader fileReader) {
        this(DEFAULT_TAG_NAME, fileReader);
    }

    public IncludeTagHandler(String tagName, TemplateFileReader fileReader) {
        super(tagName);
        this.fileReader = fileReader;
    }

    @Override
    public String handle(Map<String, Object> model, Map<String, String> attributes, String children) {
        String templateName = attributes.get(TEMPLATE_ATTRIBUTE_NAME);
        if (templateName == null || templateName.isBlank()) {
            return "";
        }
        String templateString = fileReader.read(templateName);
        return engine.render(templateString, model);
    }
}
