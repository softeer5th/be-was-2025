package webserver.writer.html;

import java.util.*;

public class HTMLElement{
    public static class Builder {
        public static HTMLElement value(String value) {
            return new HTMLElement(value);
        }
        public static Builder tag(HTMLTag tag) {
            return new Builder(tag);
        }

        private HTMLTag tag;
        private Map<String, String> attributes = new HashMap<>();

        private List<HTMLElement> children = new ArrayList<>();
        private Builder(HTMLTag tag) {
            this.tag = tag;
        }
        public Builder className(String className) {
            attributes.put("class", className);
            return this;
        }
        public Builder href(String href) {
            attributes.put("href", href);
            return this;
        }
        public Builder src(String src) {
            attributes.put("src", src);
            return this;
        }

        public Builder appendChild(HTMLElement child) {
            this.children.add(child);
            return this;
        }

        public HTMLElement build() {
            return new HTMLElement(tag, attributes, children);
        }
    }

    private final HTMLTag tag;
    private String value;
    final Map<String, String> attributes;
    private List<HTMLElement> children;

    private HTMLElement(HTMLTag tag, Map<String, String> attributes, List<HTMLElement> children) {
        this.tag = tag;
        this.attributes = attributes;
        this.children = children;
    }

    private HTMLElement(String value) {
        this.tag = HTMLTag.PLAIN;
        this.value = value;
        this.attributes = new HashMap<>();
    }

    public HTMLTag getTag() {
        return tag;
    }
    public Map<String, String> getAttributes() {
        return attributes;
    }
    public List<HTMLElement> getChildren() {
        return children;
    }
    public String getValue() {
        return value;
    }
}
