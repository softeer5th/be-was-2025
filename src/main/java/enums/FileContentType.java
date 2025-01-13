package enums;

import exception.ClientErrorException;

import static exception.ErrorCode.UNSUPPORTED_FILE_EXTENSION;

public enum FileContentType {
    HTML_UTF_8("text/html; charset=utf-8"),
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
        int dotIndex = path.toLowerCase().lastIndexOf('.');
        String extension = path.substring(dotIndex + 1);
        return switch (extension) {
            case "html" -> HTML_UTF_8;
            case "css" -> CSS;
            case "js" -> JS;
            case "ico" -> ICO;
            case "svg" -> SVG;
            case "png" -> PNG;
            case "jpg" -> JPG;
            default -> throw new ClientErrorException(UNSUPPORTED_FILE_EXTENSION);
        };
    }
}