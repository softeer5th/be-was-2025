package webserver.view.renderer;

import util.HtmlEscapeUtil;
import util.ReflectionUtil;
import webserver.view.TagRenderer;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <pre>
 *  my-text 태그를 렌더링하는 클래스
 *  childrenTemplate에 있는 ${}를 model에 있는 값으로 치환해준다.
 * </pre>
 */
public class TextTagRenderer extends TagRenderer {
    public static final String DEFAULT_TAG_NAME = "my-text";

    private static final Pattern PATH_PATTERN = Pattern.compile("\\$\\{([a-zA-Z0-9.]+)\\}");

    public TextTagRenderer() {
        this(DEFAULT_TAG_NAME);
    }

    public TextTagRenderer(String tagName) {
        super(tagName);
    }

    // <my-text>${user.name}</my-text>
    @Override
    public String handle(Map<String, Object> model, Map<String, String> tagAttributes, String childrenTemplate) {
        StringBuilder sb = new StringBuilder();

        Matcher matcher = PATH_PATTERN.matcher(childrenTemplate);
        while (matcher.find()) {
            String path = matcher.group(1);
            String value = ReflectionUtil.recursiveCallGetter(model, path).orElse("").toString();
            // XSS 방어를 위해 escape 처리
            value = HtmlEscapeUtil.escapeHtml(value);
            matcher.appendReplacement(sb, value);
        }
        matcher.appendTail(sb);
        return engine.render(sb.toString(), model);
    }

}
