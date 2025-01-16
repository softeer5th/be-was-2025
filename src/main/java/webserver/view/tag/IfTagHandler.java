package webserver.view.tag;

import util.ReflectionUtil;
import webserver.view.TagHandler;
import webserver.view.TemplateEngine;

import java.util.Map;
import java.util.Optional;

public class IfTagHandler extends TagHandler {
    public static final String IF_TAG_NAME = "my-if";
    public static final String CONDITION_ATTRIBUTE_NAME = "condition";
    private static final String OPERATOR_PATTERN = "(&&)|(\\|\\|)";
    private TemplateEngine engine;

    public IfTagHandler() {
        this(IF_TAG_NAME);
    }

    public IfTagHandler(String tagName) {
        super(tagName);
    }

    @Override
    public void setEngine(TemplateEngine engine) {
        this.engine = engine;
    }


    // <my-if condition="true">children</my-if>
    @Override
    public String handle(Map<String, Object> model, Map<String, String> attributes, String children) {
        String condition = attributes.get(CONDITION_ATTRIBUTE_NAME);
        if (isConditionTrue(model, condition)) {
            return engine.render(children, model);
        }
        return "";
    }

    // user.name && !user.isAdmin
    // && 와 || 의 우선순위는 앞에 있는 것이 높다.
    public boolean isConditionTrue(Map<String, Object> model, String condition) {
        condition = condition.strip();
        String[] tokens = condition.split(OPERATOR_PATTERN, 2);
        if (tokens.length == 1) {
            // tokens[0] == condition 인 상황
            if (condition.startsWith("!")) {
                return !isConditionTrue(model, condition.substring(1));
            }

            if ("true".equalsIgnoreCase(condition))
                return true;
            else if ("false".equalsIgnoreCase(condition))
                return false;

            tokens = condition.split("\\.");
            Object cursor = model.get(tokens[0]);
            // condition이 .(dot) 로 구분된 경로가 아니라 단일 값일 때는 해당하는 값이 null인지 판단
            if (tokens.length == 1)
                return cursor != null;
            //  condition이 .(dot) 로 구분된 경로가면 객체 탐색
            for (int i = 1; i < tokens.length; i++) {
                String fieldName = tokens[i];
                Optional<Object> getter = ReflectionUtil.getter(cursor, fieldName);
                if (getter.isEmpty())
                    return false;
                cursor = getter.get();
            }
            return cursor != null;

        } else {
            // condition에 && 또는 || 가 포함된 상황
            String first = tokens[0].strip();
            String operator = condition.substring(first.length(), first.length() + 2);
            String rest = tokens[1].strip();
            return switch (operator) {
                case "&&" -> isConditionTrue(model, first) && isConditionTrue(model, rest);
                case "||" -> isConditionTrue(model, first) || isConditionTrue(model, rest);
                default -> false;
            };
        }
    }
}
