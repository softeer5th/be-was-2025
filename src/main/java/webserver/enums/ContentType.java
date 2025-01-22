package webserver.enums;

/**
 * Content Type을 나타내는 enum
 */
public enum ContentType {
    TEXT_PLAIN("text/plain", "txt"),
    TEXT_HTML("text/html", "html"),
    TEXT_CSS("text/css", "css"),
    IMAGE_JPEG("image/jpeg", "jpeg"),
    IMAGE_JPG("image/jpeg", "jpg"),
    IMAGE_PNG("image/png", "png"),
    IMAGE_GIF("image/gif", "gif"),
    IMAGE_SVG("image/svg+xml", "svg"),
    IMAGE_ICO("image/vnd.microsoft.icon", "ico"),
    APPLICATION_JAVASCRIPT("application/javascript", "js"),
    APPLICATION_JSON("application/json", "json"),
    APPLICATION_XML("application/xml", "xml"),
    APPLICATION_OCTET_STREAM("application/octet-stream", "bin"),
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded", null),
    MULTIPART_FORM_DATA("multipart/form-data", null);

    // MIME-TYPE
    public final String mimeType;
    // 파일 확장자
    public final String fileExtension;

    ContentType(String type, String extension) {
        this.mimeType = type;
        this.fileExtension = extension;
    }

    /**
     * 파일 확장자를 이용해 세부 Content Type을 결정해 반환
     *
     * @param extension 파일 확장자
     * @return Content Type. 없다면 binary 파일로 처리
     */
    public static ContentType of(String extension) {
        for (ContentType contentType : values()) {
            if (contentType.fileExtension != null &&
                contentType.fileExtension.equals(extension)) {
                return contentType;
            }
        }
        // 기본적으로 binary 파일로 처리
        return APPLICATION_OCTET_STREAM;
    }

    /**
     * MIME-TYPE을 이용해 Content Type을 결정해 반환
     *
     * @param type MIME-TYPE 형식 문자열
     * @return 일치 여부
     */
    public boolean equals(String type) {
        return this.mimeType.equals(type);
    }
}
