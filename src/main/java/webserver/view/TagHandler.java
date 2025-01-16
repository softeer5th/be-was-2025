package webserver.view;

import java.util.Map;

// <html>
// <my-if condition="session.user">
//    <div>로그인 되어 있습니다.</div>
//</my-if>
//</html>
public abstract class TagHandler {
    protected final String tagName;
    protected TemplateEngine engine;

    protected TagHandler(String tagName) {
        this.tagName = tagName;
    }

    public void setEngine(TemplateEngine engine) {
        this.engine = engine;
    }

    public abstract String handle(Map<String, Object> model, Map<String, String> attributes, String children);

    public String getTagName() {
        return tagName;
    }
}
