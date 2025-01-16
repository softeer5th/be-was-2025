package webserver.view.tag;

import webserver.exception.InternalServerError;
import webserver.view.TagHandler;
import webserver.view.TemplateEngine;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

public class ForeachTagHandler extends TagHandler {
    public static final String DEFAULT_TAG_NAME = "my-foreach";
    public static final String ITEMS_ATTRIBUTE_NAME = "items";
    public static final String ITEM_ATTRIBUTE_NAME = "item";

    private TemplateEngine engine;

    public ForeachTagHandler() {
        this(DEFAULT_TAG_NAME);
    }

    public ForeachTagHandler(String tagName) {
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
        Object itemsObject = model.get(itemsAttribute);
        Iterator<?> iterator = null;
        if (itemsObject instanceof Iterable<?> iterable)
            iterator = iterable.iterator();
        else if (itemsObject instanceof Object[] array)
            iterator = Arrays.stream(array).iterator();
        else
            throw new InternalServerError(ITEMS_ATTRIBUTE_NAME + " attribute가 Iterable이나 배열이 아닙니다.");


        for (Object item : (Iterable<?>) model.get(itemsAttribute)) {
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
}
