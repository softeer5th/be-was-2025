package http;

import java.util.HashMap;
import java.util.Map;

public enum MimeType {
    TEXT_PLAIN("", "text/plain"),
    TEXT_HTML("html", "text/html"),
    TEXT_CSS("css", "text/css"),
    TEXT_JAVASCRIPT("js", "text/js"),
    IMAGE_VND_MICROSOFT_ICON("ico", "image/vnd.microsoft.icon"),
    IMAGE_JPEG("jpeg", "image/jpeg"),
    IMAGE_PNG("png", "image/png"),
    IMAGE_SVG_XML("svg", "image/svg+xml");

    private String extension;
    private final String type;
    private static final Map<String, MimeType> extensionToMimeMap = new HashMap<>();
    private static final String defaultMimeType = "application/octet-stream";

    static{
        for(MimeType mimeType : MimeType.values()){
            extensionToMimeMap.put(mimeType.extension, mimeType);
        }
    }

    MimeType(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static String getMimeType(String extension){
        MimeType mimeType = extensionToMimeMap.get(extension);
        if(mimeType != null){
            return mimeType.type;
        }
        return defaultMimeType;
    }
}
