package webserver.view;

import java.util.Map;

/**
 * 특정 커스텀 태그를 렌더링하는 클래스
 */
public abstract class TagRenderer {
    // 태그의 이름. my-로 시작해야 함
    protected final String tagName;
    // 태그 랜더링 시 children template을 재귀적으로 처리하기 위해 필요한 TemplateEngine
    protected TemplateEngine engine;

    /**
     * 생성자
     *
     * @param tagName 렌더링을 담당할 커스텀 태그 이름
     */
    protected TagRenderer(String tagName) {
        this.tagName = tagName;
    }

    /**
     * 자식 템플릿 문자열을 렌더링 시 사용할 TemplateEngine을 설정
     *
     * @param engine TemplateEngine
     */
    public void setEngine(TemplateEngine engine) {
        this.engine = engine;
    }

    /**
     * 커스텀 태그를 렌더링한다.
     *
     * @param model            렌더링에 사용할 데이터
     * @param tagAttributes    태그의 속성
     * @param childrenTemplate 태그 내부의 자식 템플릿 문자열
     * @return 렌더링 결과 html
     */
    // 태그를 렌더링하여 반환
    public abstract String handle(Map<String, Object> model, Map<String, String> tagAttributes, String childrenTemplate);

    /**
     * 담당하는 커스텀 태그의 이름을 반환한다.
     *
     * @return 커스텀 태그 이름
     */
    public String getTagName() {
        return tagName;
    }
}
