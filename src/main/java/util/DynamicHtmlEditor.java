package util;

public class DynamicHtmlEditor {
    private static final String DYNAMIC_PREFIX = "dynamic";

    public static String edit(String content, String field, String value) {
        String target = String.format("{{ %s:%s }}", DYNAMIC_PREFIX, field);
        StringBuilder sb = new StringBuilder(content);

        int index = sb.indexOf(target);
        if (index == -1) {
            return content;
        }

        sb.replace(index, index + target.length(), value);
        return sb.toString();
    }
}
