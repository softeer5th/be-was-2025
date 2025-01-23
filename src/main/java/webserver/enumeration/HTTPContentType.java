package webserver.enumeration;

public enum HTTPContentType {
    ALL("*", "*"),
    TEXT("text", "*"),
    TEXT_PLAIN("text", "plain"),
    TEXT_HTML("text", "html", "htm"),
    TEXT_CSS("text", "css"),
    TEXT_JAVASCRIPT("text", "javascript"),

    IMAGE("image", "*"),
    IMAGE_PNG("image", "png"),
    IMAGE_JPEG("image", "jpeg", "jpe", "jpg"),
    IMAGE_GIF("image", "gif"),
    IMAGE_ICON("image", "x-icon", "ico"),
    IMAGE_SVG("image", "svg+xml", "svg"),
    IMAGE_WEBP("image", "webp"),
    IMAGE_APNG("image", "apng"),
    IMAGE_AVIF("image", "avif"),
    MULTIPART_FORM_DATA("multipart", "form-data"),
    APPLICATION_JSON("application", "json"),
    APPLICATION_URLENCODED("application", "x-www-form-urlencoded"),
    APPLICATION_OCTET_STREAM("application", "octet-stream");

    public final String primary;
    public final String detail;
    public final String [] alias;

    public static HTTPContentType DEFAULT_TYPE() {
        return APPLICATION_OCTET_STREAM;
    }

    HTTPContentType(String primary, String detail, String ... alias) {
        this.primary = primary;
        this.detail = detail;
        this.alias = alias;
    }

    @Override
    public String toString() {
        return primary + "/" + detail;
    }

    public static HTTPContentType fromDetailType(String postfix) {
        for (HTTPContentType contentType : values()) {
            if (contentType.detail.equals(postfix)) {
                return contentType;
            }
            if (contentType.alias != null) {
                for (String alias : contentType.alias) {
                    if (alias.equals(postfix)) {
                        return contentType;
                    }
                }
            }
        }
        return DEFAULT_TYPE();
    }

    public static HTTPContentType fromFullType(String accept) {
        String [] types = accept.split("/");
        if (types.length == 2) {
            return fromDetailType(types[1]);
        }
        return DEFAULT_TYPE();
    }

    public static boolean isSupported(String accept) {
        return fromFullType(accept) != DEFAULT_TYPE();
    }
}
