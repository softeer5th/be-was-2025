package http.response;

import http.request.HttpRequest;
import model.Comment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

public class DynamicHtmlBuilder {
    private StringBuilder htmlBuilder = new StringBuilder();
    private String html;
    private HttpRequest request;
    private Map<String, Object> replaceMap;

    private static final Logger logger = LoggerFactory.getLogger(DynamicHtmlBuilder.class);

    public DynamicHtmlBuilder(String html, HttpRequest request, Map<String, Object> replaceMap) {
        this.html = html;
        this.request = request;
        this.replaceMap = replaceMap;
    }

    public String build() {
        String[] lines = html.split("\n");
        for (String line : lines) {
            if (line.contains("{{") && line.contains("}}")) {
                String tag = line.substring(line.indexOf("{{")+2, line.indexOf("}}"));
                if (replaceMap.containsKey(tag)) {
                    String newLine = replaceTag(line, tag, replaceMap.get(tag));
                    htmlBuilder.append(newLine);
                }
            } else {
                htmlBuilder.append(line);
            }
            htmlBuilder.append("\n");
        }
        return htmlBuilder.toString();
    }

    public String replaceTag(String line, String tag, Object content) {
        return switch (tag.toLowerCase()) {
            case "username", "article", "articleid" -> replaceText(line, tag, content.toString());
            case "articlephoto" -> replaceArticlePhoto(line, content.toString());
            case "comments" -> replaceComments(line, (List<Comment>) content);
            default -> "Unknown";
        };
    }

    public String replaceText(String line, String tag, String username) {
        return line.replace("{{"+tag+"}}", username);
    }

    public String replaceArticlePhoto(String line, String photo) {
        logger.debug("Photo: {} ", photo);
        return line.replace("{{articlephoto}}", "src=\""+photo+"\"");
    }

    public String replaceComments(String line, List<Comment> content) {
        StringBuilder commentBuilder = new StringBuilder();
        for (Comment comment : content) {
            commentBuilder
                    .append("""
                              <li class="comment__item">
                               <div class="comment__item__user">
                                 <img class="comment__item__user__img" />
                                 <p class="comment__item__user__nickname">""")
                    .append(comment.userId())
                    .append("""
                               </p>
                               </div>
                               <p class="comment__item__article">\n
                    """);
            commentBuilder.append(comment.content()).append("\n");
            commentBuilder.append("        </p>");
            commentBuilder.append("</li>\n");
        }
        return commentBuilder.toString();
    }
}
