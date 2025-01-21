package webserver.writer.html;

import java.util.Map;
import java.util.Set;

public class HTMLWriter {
    private static void renderRecur(HTMLElement root, StringBuilder sb) {
        if (root.getTag() == HTMLTag.PLAIN) {
            sb.append(root.getValue());
            return;
        }
        sb.append("<");
        sb.append(root.getTag());
        Set<Map.Entry<String, String >> attr = root.getAttributes().entrySet();
        for (Map.Entry<String, String> entry : attr) {
            sb.append(" ");
            sb.append(entry.getKey());
            sb.append("=\"");
            sb.append(entry.getValue());
            sb.append("\"");
        }
        sb.append(">\n");
        for (HTMLElement child : root.getChildren()) {
            renderRecur(child, sb);
        }
        sb.append("\n</");
        sb.append(root.getTag());
        sb.append(">");
    }

    public static String render(HTMLElement element) {
        StringBuilder sb = new StringBuilder();
        renderRecur(element, sb);
        return sb.toString();
    }
}
