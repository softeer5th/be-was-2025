package webserver.view.renderer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.ReflectionUtil;
import webserver.exception.InternalServerError;
import webserver.view.TagRenderer;
import webserver.view.TemplateEngine;

import java.util.Map;
import java.util.Objects;

// 조건문이 참이면 childTemplate을 렌더링하고, 거짓이면 빈 문자열을 반환하는 TagRenderer
// 지원하는 조건문 형식은 다음과 같다.
// 1. boolean literal : true, false
// 2. 객체 탐색 경로 : session.user.isAdmin
// 3. not 연산자 : !session.user.isAdmin
// 4. 이항 연산자 : session.user.isAdmin && session.user.isLogin
// 5. 이항 연산자 : session.user.isAdmin || session.user.isLogin
public class IfTagRenderer extends TagRenderer {
    public static final String IF_TAG_NAME = "my-if";
    public static final String CONDITION_ATTRIBUTE_NAME = "condition";
    private static final String LOGICAL_BINARY_OPERATOR_PATTERN = "(&&)|(\\|\\|)";
    private static final String COMPARE_BINARY_OPERATOR_PATTERN = "(>=)|(<=)|(>)|(<)|(==)|(!=)";
    private static final String NOT_OPERATOR = "!";
    private static final String EMPTY_STRING = "";
    private static final Logger log = LoggerFactory.getLogger(IfTagRenderer.class);
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

    @Override
    public String handle(Map<String, Object> model, Map<String, String> tagAttributes, String childrenTemplate) {
        String condition = tagAttributes.get(CONDITION_ATTRIBUTE_NAME);
        if (isConditionTrue(model, condition)) {
            return engine.render(childrenTemplate, model);
        }
        return EMPTY_STRING;
    }

    // condition을 평가하여 참이면 true, 거짓이면 false를 반환하는 메서드
    private boolean isConditionTrue(Map<String, Object> model, String condition) {
        condition = condition.strip();
        // condition을 맨 앞에 오는 이항 연산자 기준으로 나누기
        String[] tokens = condition.split(LOGICAL_BINARY_OPERATOR_PATTERN, 2);
        if (tokens.length == 1) {
            // tokens[0] == condition 인 상황

            // condition에 비교 연산자가 포함된 경우
            String[] compareTokens = condition.split(COMPARE_BINARY_OPERATOR_PATTERN);
            if (compareTokens.length > 2)
                throw new InternalServerError("비교 연산자가 잘못 사용되었습니다.");
            if (compareTokens.length == 2) {
                String left = compareTokens[0].strip();
                String right = compareTokens[1].strip();
                String operator = condition.substring(left.length(), condition.length() - right.length()).strip();
                log.debug("compare operator. left:{}, operator:{}, right:{}", left, operator, right);
                return switch (operator) {
                    case ">=" -> parseInt(model, left) >= parseInt(model, right);
                    case "<=" -> parseInt(model, left) <= parseInt(model, right);
                    case ">" -> parseInt(model, left) > parseInt(model, right);
                    case "<" -> parseInt(model, left) < parseInt(model, right);
                    case "==" -> Objects.equals(parseInt(model, left), parseInt(model, right));
                    case "!=" -> !Objects.equals(parseInt(model, left), parseInt(model, right));
                    default -> throw new InternalServerError("비교 연산자가 잘못 사용되었습니다. 연산자:" + operator);
                };
            }

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
            return ReflectionUtil.recursiveCallGetter(model, condition).isPresent();

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

    private Integer parseInt(Map<String, Object> model, String condition) {
        // 정수로 변환 가능한 경우 Optional로 감싸서 반환
        try {
            return Integer.parseInt(condition);
        } catch (NumberFormatException ignored) {
        }
        // 정수로 변환할 수 없는 경우 객체 탐색 경로로 간주
        return ReflectionUtil.recursiveCallGetter(model, condition)
                .filter(o -> o instanceof Integer)
                .map(o -> (Integer) o)
                .orElseThrow(() -> new InternalServerError("정수로 변환할 수 없는 객체 탐색 경로입니다."));
    }

}
