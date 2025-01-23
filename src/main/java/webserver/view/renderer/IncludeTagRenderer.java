package webserver.view.renderer;

import webserver.view.TagRenderer;
import webserver.view.TemplateFileReader;

import java.util.Map;

/**
 * <pre>
 *  my-include 태그를 렌더링하는 클래스
 *  template 속성에 해당하는 파일을 읽어서 렌더링해준다.
 * </pre>
 */
public class IncludeTagRenderer extends TagRenderer {
    public static final String DEFAULT_TAG_NAME = "my-include";
    public static final String TEMPLATE_ATTRIBUTE_NAME = "template";
    private static final String EMPTY_STRING = "";
    private final TemplateFileReader fileReader;

    public IncludeTagRenderer(TemplateFileReader fileReader) {
        this(DEFAULT_TAG_NAME, fileReader);
    }

    public IncludeTagRenderer(String tagName, TemplateFileReader fileReader) {
        super(tagName);
        this.fileReader = fileReader;
    }

    @Override
    public String handle(Map<String, Object> model, Map<String, String> tagAttributes, String childrenTemplate) {
        String templateName = tagAttributes.get(TEMPLATE_ATTRIBUTE_NAME);
        if (templateName == null || templateName.isBlank()) {
            return EMPTY_STRING;
        }
        String templateString = fileReader.read(templateName);
        return engine.render(templateString, model);
    }
}
