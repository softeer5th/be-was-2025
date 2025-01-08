package model;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum Mime {
    CSS("css","text/css"),
    HTML("html","text/html"),
    SVG("svg", "image/svg+xml"),
    PNG("png","image/png"),
    JPG("jpg","image/jpeg"),
    JS("js","text/javascript"),
    ICO("ico","image/x-icon");

    private final String extension;
    private final String contentType;

    Mime(String extension, String contentType) {
        this.extension = extension;
        this.contentType = contentType;
    }

    private static final Map<String, Mime> BY_EXTENSION =
            Stream.of(values()).collect(Collectors.toMap(Mime::getExtension, e -> e));

    public String getContentType() {
        return contentType;
    }

    public String getExtension() {
        return extension;
    }

    public static Mime getByExtension(String extension) {
        return BY_EXTENSION.get(extension);
    }
}
