package util;

import java.util.HashMap;
import java.util.Map;

public class MimeTypeMapper {
    private final Map<String, String> mapper = new HashMap<>();

    public MimeTypeMapper() {
        this.mapper.put(MimeType.HTML.getExtension(), MimeType.HTML.getMimeType());
        this.mapper.put(MimeType.CSS.getExtension(), MimeType.CSS.getMimeType());
        this.mapper.put(MimeType.JS.getExtension(), MimeType.JS.getMimeType());
        this.mapper.put(MimeType.PNG.getExtension(), MimeType.PNG.getMimeType());
        this.mapper.put(MimeType.JPG.getExtension(), MimeType.JPG.getMimeType());
        this.mapper.put(MimeType.ICO.getExtension(), MimeType.ICO.getMimeType());
        this.mapper.put(MimeType.SVG.getExtension(), MimeType.SVG.getMimeType());
    }

    public String getMimeType(String extension) {
        return mapper.getOrDefault(extension, "*/*");
    }
}
