package model;

import java.util.HashMap;
import java.util.Map;

public enum Mime {
    TEXT_HTML("html", "text/html"),
    TEXT_CSS("css", "test/css"),
    TEXT_JAVASCRIPT("js", "text/js"),
    IMAGE_VND_MICROSOFT_ICON("ico", "image/vnd.microsoft.icon"),
    IMAGE_JPEG("jpeg", "image/jpeg"),
    IMAGE_PNG("png", "image/png");

    private String extension;
    private final String type;
    private static final Map<String, Mime> extensionToMimeMap = new HashMap<>();

    static{
        for(Mime mime: Mime.values()){
            extensionToMimeMap.put(mime.extension, mime);
        }
    }

    Mime(String extension, String type) {
        this.extension = extension;
        this.type = type;
    }

    public static String getMimeType(String extension){
        Mime mime = extensionToMimeMap.get(extension);
        if(mime != null){
            return mime.type;
        }
        return null;
    }
}
