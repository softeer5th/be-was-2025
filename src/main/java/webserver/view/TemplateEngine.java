package webserver.view;

import java.util.Map;

// template을 렌더링하는 인터페이스
public interface TemplateEngine {

    // template에 model을 렌더링한 html을 반환하는 메서드
    String render(String template, Map<String, Object> model);

}
