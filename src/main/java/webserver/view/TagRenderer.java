package webserver.view;

import java.util.Map;

// 특정 태그의 렌더링을 담당하는 클래스
public abstract class TagRenderer {
    // 태그의 이름. my-로 시작해야 함
    protected final String tagName;
    // 태그 랜더링 시 children template을 재귀적으로 처리하기 위해 필요한 TemplateEngine
    protected TemplateEngine engine;

    protected TagRenderer(String tagName) {
        this.tagName = tagName;
    }

    public void setEngine(TemplateEngine engine) {
        this.engine = engine;
    }

    // 태그를 렌더링하여 반환
    public abstract String handle(Map<String, Object> model, Map<String, String> tagAttributes, String childrenTemplate);

    public String getTagName() {
        return tagName;
    }
}
