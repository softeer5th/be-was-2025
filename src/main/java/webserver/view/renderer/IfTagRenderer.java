package webserver.view.renderer;

import util.ReflectionUtil;
import webserver.view.TagRenderer;
import webserver.view.TemplateEngine;

import java.util.Map;

public class IfTagRenderer extends TagRenderer {
    public static final String IF_TAG_NAME = "my-if";
    public static final String CONDITION_ATTRIBUTE_NAME = "condition";
    private static final String BINARY_OPERATOR_PATTERN = "(&&)|(\\|\\|)";
    private static final String NOT_OPERATOR = "!";
    private TemplateEngine engine;

    public IfTagRenderer() {
        this(IF_TAG_NAME);
    }

    public IfTagRenderer(String tagName) {
        super(tagName);
    }

    @Override
    public void setEngine(TemplateEngine engine) {
        this.engine = engine;
    }


    // <my-if condition="session.user">children</my-if>
    @Override
    public String handle(Map<String, Object> model, Map<String, String> tagAttributes, String childrenTemplate) {
        String condition = tagAttributes.get(CONDITION_ATTRIBUTE_NAME);
        if (isConditionTrue(model, condition)) {
            return engine.render(childrenTemplate, model);
        }
        return "";
    }
    
    // && 와 || 의 우선순위는 앞에 있는 것이 높다.
    public boolean isConditionTrue(Map<String, Object> model, String condition) {
        condition = condition.strip();
        // condition을 맨 앞에 오는 이항 연산자 기준으로 나누기
        String[] tokens = condition.split(BINARY_OPERATOR_PATTERN, 2);
        if (tokens.length == 1) {
            // tokens[0] == condition 인 상황

            // condition에 not 연산자가 포함된 경우
            if (condition.startsWith(NOT_OPERATOR)) {
                return !isConditionTrue(model, condition.substring(1));
            }

            // condition 이 boolean literal인 경우
            if ("true".equalsIgnoreCase(condition))
                return true;
            else if ("false".equalsIgnoreCase(condition))
                return false;

            // condition 이 객체 탐색 경로인 경우 ex) session.user.isAdmin
            String objectTraversalPath = condition;

            return ReflectionUtil.recursiveCallGetter(model, objectTraversalPath).isPresent();

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
