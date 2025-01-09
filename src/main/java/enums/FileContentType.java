package enums;

public enum FileContentType {
    HTML("text/html"),
    CSS("text/css"),
    JS("text/javascript"),
    ICO("image/x-icon"),
    SVG("image/svg+xml"),
    PNG("image/png"),
    JPG("image/jpeg");

    private final String contentType;

    FileContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getContentType() {
        return contentType;
    }

    public static FileContentType getExtensionFromPath(String path) {
        if (path.endsWith(".html")) {
            return HTML;
        } else if (path.endsWith(".css")) {
            return CSS;
        } else if (path.endsWith(".js")) {
            return JS;
        } else if (path.endsWith(".ico")) {
            return ICO;
        } else if (path.endsWith(".svg")) {
            return SVG;
        } else if (path.endsWith(".png")) {
            return PNG;
        } else if (path.endsWith(".jpg")) {
            return JPG;
        } else {
            throw new IllegalArgumentException("지원하지 않는 파일 확장자 형식입니다.");
        }
    }
}