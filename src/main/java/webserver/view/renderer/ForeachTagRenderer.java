package webserver.view.renderer;

import webserver.exception.InternalServerError;
import webserver.view.TagRenderer;
import webserver.view.TemplateEngine;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

import static util.ReflectionUtil.recursiveCallGetter;

/**
 * <pre>
 * my-foreach 커스텀 태그를 렌더링하는 클래스
 * items 속성에 Iterable이나 배열을 넣으면 childrenTemplate을 items의 갯수만큼 렌더링해준다.
 * 각 렌더링 시 item 속성을 model에 추가하여 childTemplate에서 item을 사용할 수 있게 한다. 또한 index 속성을 추가하면 반복 index도 사용할 수 있다.
 * </pre>
 */
// childrenTemplate을 items의 갯수만큼 렌더링해주는 TagRenderer.
// 각 렌더링 시 item을 model에 추가하여 childTemplate에서 item을 사용할 수 있게 한다.
public class ForeachTagRenderer extends TagRenderer {
    public static final String DEFAULT_TAG_NAME = "my-foreach";
    // items: 반복할 대상
    public static final String ITEMS_ATTRIBUTE_NAME = "items";
    // item: 각 반복 시 사용할 이름
    public static final String ITEM_ATTRIBUTE_NAME = "item";
    // index: 각 반복 시 index를 사용할 이름
    public static final String INDEX_ATTRIBUTE_NAME = "index";

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
        String indexAttribute = tagAttributes.get(INDEX_ATTRIBUTE_NAME);
        if (model.containsKey(itemAttribute))
            throw new InternalServerError(ITEM_ATTRIBUTE_NAME + " attribute가 이미 모델에 있습니다.");

        StringBuilder result = new StringBuilder();
        Optional<Object> itemsObject = recursiveCallGetter(model, itemsAttribute);
        if (itemsObject.isEmpty())
            throw new InternalServerError(ITEMS_ATTRIBUTE_NAME + " attribute가 모델에 없습니다.");
        Iterator<?> iterator = resolveIterator(itemsObject.get());

        for (int i = 0; iterator.hasNext(); i++) {
            Object item = iterator.next();
            model.put(itemAttribute, item);
            if (indexAttribute != null)
                model.put(indexAttribute, i);
            result.append(engine.render(childrenTemplate, model));
        }
        model.remove(itemAttribute);
        if (indexAttribute != null)
            model.remove(indexAttribute);
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
