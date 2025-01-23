package webserver.view;

import java.util.Map;

/**
 * 템플릿 문자열과 데이터를 이용해 html을 렌더링하는 엔진
 */
public interface TemplateEngine {

    /**
     * 템플릿을 렌더링하여 html을 반환한다.
     *
     * @param template 템플릿 문자열
     * @param model    렌더링에 사용할 데이터
     * @return 렌더링된 html
     */
    String render(String template, Map<String, Object> model);

}
