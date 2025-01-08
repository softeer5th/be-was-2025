package model;

import java.util.Map;

public class ContentTypeMapper {
    private final Map<String, String> MAPPER;

    public ContentTypeMapper() {
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

    public String getContentType(String fileType) {
        return MAPPER.get(fileType);
    }
}