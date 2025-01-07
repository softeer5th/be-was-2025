package webserver.enums;

public enum ContentType {
    TEXT_PLAIN("text/plain", "txt"),
    TEXT_HTML("text/html", "html"),
    TEXT_CSS("text/css", "css"),
    IMAGE_JPEG("image/jpeg", "jpeg"),
    IMAGE_PNG("image/png", "png"),
    IMAGE_GIF("image/gif", "gif"),
    IMAGE_SVG("image/svg+xml", "svg"),
    IMAGE_ICO("image/vnd.microsoft.icon", "ico"),
    APPLICATION_JAVASCRIPT("application/javascript", "js"),
    APPLICATION_JSON("application/json", "json"),
    APPLICATION_XML("application/xml", "xml");

    // MIME-TYPE
    public final String mimeType;
    // 파일 확장자
    public final String fileExtension;

    ContentType(String type, String extension) {
        this.mimeType = type;
        this.fileExtension = extension;
    }

    // 파일 확장자를 이용해 세부 Content Type을 결정해 반환
    public static ContentType of(String extension) {
        for (ContentType contentType : values()) {
            if (contentType.fileExtension.equals(extension)) {
                return contentType;
            }
        }
        return TEXT_PLAIN;
    }
}
