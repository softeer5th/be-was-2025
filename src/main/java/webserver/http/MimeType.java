package webserver.http;

import java.util.HashMap;
import java.util.Map;

public class MimeType {
    private static final Map<String, String> mimeMapping = new HashMap<>();
    private static final String DEFAULT_MIMETYPE= "application/octet-stream";

    static {
        mimeMapping.put("html", "text/html");
        mimeMapping.put("css", "text/css");
        mimeMapping.put("js", "text/javascript");
        mimeMapping.put("ico", "image/x-icon");
        mimeMapping.put("png", "image/png");
        mimeMapping.put("jpg", "image/jpeg");
        mimeMapping.put("svg", "image/svg+xml");
    }

    public static String getMimeType(String fileName) {
        String extension = fileName.trim().toLowerCase().substring(fileName.lastIndexOf(".") + 1);
        return mimeMapping.getOrDefault(extension, DEFAULT_MIMETYPE);
    }
}


