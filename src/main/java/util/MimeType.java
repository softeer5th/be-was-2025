package util;

public enum MimeType {
    HTML("html", "text/html"),
    CSS("css", "text/css"),
    JS("js", "text/js"),
    PNG("png", "image/png"),
    JPG("jpg", "image/jpg"),
    ICO("ico", "image/x-icon"),
    SVG("svg", "image/svg+xml");

    private final String extension;
    private final String mimeType;

    MimeType(String extension, String mimeType) {
        this.extension = extension;
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public String getExtension() {
        return extension;
    }
}
