package http.response;

import http.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class DynamicHtmlBuilder {
    private StringBuilder htmlBuilder = new StringBuilder();
    private String html;
    private HttpRequest request;
    private Map<String, String> replaceMap;

    private static final Logger logger = LoggerFactory.getLogger(DynamicHtmlBuilder.class);

    public DynamicHtmlBuilder(String html, HttpRequest request, Map<String, String> replaceMap) {
        this.html = html;
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
        logger.debug(htmlBuilder.toString());
        return htmlBuilder.toString();
    }

    public String replaceTag(String line, String tag, String content) {
        return switch (tag.toLowerCase()) {
            case "username", "article" -> replaceText(line, tag, content);
            case "articlephoto" -> replaceArticlePhoto(line, content);
            default -> "Unknown";
        };
    }

    public String replaceText(String line, String tag, String username) {
        return line.replace("{{"+tag+"}}", username);
    }

    public String replaceArticlePhoto(String line, String photo) {
//        byte[] photoBytes = photo.getBytes();
//        String encodedPhoto = Base64.getEncoder().encodeToString(photoBytes);
        return line.replace("{{articlephoto}}", "src=\""+photo+"\"");
    }
}
