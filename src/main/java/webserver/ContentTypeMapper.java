package webserver;

import java.util.Map;

public class ContentTypeMapper {
    private static final Map<String, String> MIME_TYPES = Map.of(
            "html", "text/html",
            "css", "text/css",
            "js", "application/javascript",
            "ico", "image/x-icon",
            "png", "image/png",
            "jpg", "image/jpeg",
            "jpeg", "image/jpeg",
            "svg", "image/svg+xml",
            "json", "application/json"
    );

    public static String getContentType(String path) {
        String extension = getExtension(path);
        return MIME_TYPES.getOrDefault(extension, "text/html");
    }

    private static String getExtension(String path) {
        int lastDot = path.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return path.substring(lastDot + 1).toLowerCase();
    }
}