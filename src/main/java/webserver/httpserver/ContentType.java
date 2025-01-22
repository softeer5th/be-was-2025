package webserver.httpserver;

import exception.FileNotSupportedException;

public enum ContentType {

    HTML(new String[] {"html", "htm"}, "text/html; charset=utf-8"),
    CSS(new String[] {"css"},          "text/css; charset=utf-8"),
    JS(new String[] {"js"},            "application/javascript"),
    PNG(new String[] {"png"},          "image/png"),
    JPG(new String[] {"jpg", "jpeg"},  "image/jpeg"),
    GIF(new String[] {"gif"},          "image/gif"),
    JSON(new String[] {"json"},        "application/json"),
    SVG(new String[] {"svg", "xml"},   "image/svg+xml"),
    ICO(new String[] {"ico"},          "image/x-icon"),
    OCTET_STREAM(new String[] {"bin"}, "application/octet-stream"),
    X_WWW_FORM_URLENCODED(new String[]{}, "application/x-www-form-urlencoded"),
    MULTIPART_FORM_DATA(new String[]{}, "multipart/form-data" );

    private final String[] extensions;
    private final String mimeType;

    ContentType(String[] extensions, String mimeType) {
        this.extensions = extensions;
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static String guessContentType(String uri) {
        if (uri == null) {
            throw new IllegalArgumentException("uri cannot be null");
        }

        String lowerUri = uri.toLowerCase();
        int dotIndex = lowerUri.lastIndexOf('.');
        if (dotIndex == -1) {
            return OCTET_STREAM.getMimeType();
        }

        String extension = lowerUri.substring(dotIndex + 1);

        for (ContentType contentType : values()) {
            for (String ext : contentType.extensions) {
                if (ext.equals(extension)) {
                    return contentType.getMimeType();
                }
            }
        }

        throw new FileNotSupportedException();
    }
}
