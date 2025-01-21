package webserver.view.renderer;

import webserver.exception.InternalServerError;
import webserver.view.TagRenderer;
import webserver.view.TemplateEngine;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static util.ReflectionUtil.recursiveCallGetter;

// childrenTemplate을 items의 갯수만큼 렌더링해주는 TagRenderer.
// 각 렌더링 시 item을 model에 추가하여 childTemplate에서 item을 사용할 수 있게 한다.
public class ForeachTagRenderer extends TagRenderer {
    public static final String DEFAULT_TAG_NAME = "my-foreach";
    public static final String ITEMS_ATTRIBUTE_NAME = "items";
    public static final String ITEM_ATTRIBUTE_NAME = "item";

    private TemplateEngine engine;

    public ForeachTagRenderer() {
        this(DEFAULT_TAG_NAME);
    }

    public ForeachTagRenderer(String tagName) {
        super(tagName);
    }

    public void setEngine(TemplateEngine engine) {
        this.engine = engine;
    }

    @Override
    public String handle(Map<String, Object> model, Map<String, String> tagAttributes, String childrenTemplate) {
        String itemsAttribute = tagAttributes.get(ITEMS_ATTRIBUTE_NAME);
        String itemAttribute = tagAttributes.get(ITEM_ATTRIBUTE_NAME);
        if (model.containsKey(itemAttribute))
            throw new InternalServerError(ITEM_ATTRIBUTE_NAME + " attribute가 이미 모델에 있습니다.");

        StringBuilder result = new StringBuilder();
        Optional<Object> itemsObject = recursiveCallGetter(model, itemsAttribute);
        if (itemsObject.isEmpty())
            throw new InternalServerError(ITEMS_ATTRIBUTE_NAME + " attribute가 모델에 없습니다.");
        Iterator<?> iterator = resolveIterator(itemsObject.get());

        while (iterator.hasNext()) {
            Object item = iterator.next();
            model.put(itemAttribute, item);
            result.append(engine.render(childrenTemplate, model));
            model.remove(itemAttribute);
        }
        return result.toString();
    }

    @Override
    public String getTagName() {
        return DEFAULT_TAG_NAME;
    }

    private Iterator<?> resolveIterator(Object itemsObject) {
        if (itemsObject instanceof Iterable<?> iterable)
            return iterable.iterator();
        if (itemsObject instanceof Object[] array)
            return Arrays.stream(array).iterator();
        throw new InternalServerError(ITEMS_ATTRIBUTE_NAME + " attribute가 Iterable이나 배열이 아닙니다.");
    }
}
