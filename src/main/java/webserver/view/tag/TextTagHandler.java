package webserver.view.tag;

import util.ReflectionUtil;
import webserver.view.TagHandler;

import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* <my-text>
*   ${user.name}
*   <my-if condition="session.user">
asdasd
</my-if>
* </my-text>
*
*/
public class TextTagHandler extends TagHandler {
    public static final String DEFAULT_TAG_NAME = "my-text";

    private static final Pattern PATH_PATTERN = Pattern.compile("\\$\\{([a-zA-Z0-9.]+)\\}");

    public TextTagHandler() {
        this(DEFAULT_TAG_NAME);
    }

    public TextTagHandler(String tagName) {
        super(tagName);
    }

    // <my-text>${user.name}</my-text>
    @Override
    public String handle(Map<String, Object> model, Map<String, String> attributes, String children) {
        StringBuilder sb = new StringBuilder();

        Matcher matcher = PATH_PATTERN.matcher(children);
        while (matcher.find()) {
            String path = matcher.group(1);
            String value = traversePath(path, model);
            matcher.appendReplacement(sb, value);
        }
        matcher.appendTail(sb);
        return engine.render(sb.toString(), model);
    }

    private String traversePath(String path, Map<String, Object> model) {
        String[] tokens = path.split("\\.");
        Object cursor = model.get(tokens[0]);
        for (int i = 1; i < tokens.length; i++) {
            String fieldName = tokens[i];
            Optional<Object> getter = ReflectionUtil.getter(cursor, fieldName);
            if (getter.isEmpty()) {
                return "";
            }
            cursor = getter.get();
        }
        return cursor.toString();
    }


}
