package util;

import java.util.HashMap;
import java.util.Map;

public enum ContentType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "text/javascript"),
    ICO("ico", "image/x-icon"),
    PNG("png", "image/png"),
    JPG("jpg", "image/jpeg"),
    JPEG("jpeg", "image/jpeg"),
    SVG("svg", "image/svg+xml");

    private final String extension;
    private final String mimeType;

    private static final Map<String, ContentType> LOOKUP = new HashMap<>();

    static {
        for (ContentType ct : values()) {
            LOOKUP.put(ct.extension, ct);
        }
    }

    ContentType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public String getExtension() {
        return extension;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static String getMimeTypeByExtension(String extension) {
        ContentType ct = LOOKUP.get(extension.toLowerCase());
        return ct != null ? ct.getMimeType() : "application/octet-stream";
    }
}
