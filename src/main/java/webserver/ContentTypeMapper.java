package webserver;

import java.util.Map;

public class ContentTypeMapper {
    private static final Map<String, String> CONTENT_TYPE_MAP = Map.of(
            ".html", "text/html",
            ".css", "text/css",
            ".svg", "image/svg+xml",
            ".png", "image/png",
            ".jpg", "image/jpeg",
            ".jpeg", "image/jpeg",
            ".ico", "image/vnd.microsoft.icon",
            ".js", "application/javascript"
    );

    // Content-Type을 반환하는 메서드
    public static String getContentType(String uri) {
        int lastDotIndex = uri.lastIndexOf('.');
        // uri 확장자가 없는 경우
        if (lastDotIndex == -1) {
            return "text/html";
        }
        // uri 확장자가 있는 경우
        String extension = uri.substring(lastDotIndex);
        // 확장자가 ContentTypeMapper에서 정의한 content-type에 포함되어 있는 경우
        if (CONTENT_TYPE_MAP.containsKey(extension)) {
            return CONTENT_TYPE_MAP.get(extension);
        }
        // 확장자에 대해 알 수 없는 경우
        return "application/octet-stream";

    }
}
