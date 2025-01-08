package util;

import java.util.Map;

public class ContentTypeUtil {
    private static final Map<String, String> MAPPER;
    static {
        MAPPER = Map.of(
                "html", "text/html",
                "css", "text/css",
                "js", "application/javascript",
                "ico", "image/x-icon",
                "png", "image/png",
                "jpg", "image/jpeg",
                "jpeg", "image/jpeg",
                "svg", "image/svg+xml"
        );
    }

    private ContentTypeUtil() {
    }

    public static String getContentType(String fileType) {
        return MAPPER.get(fileType);
    }

    public static boolean isValidExtension(String fileType) {
        return MAPPER.containsKey(fileType);
    }
}
