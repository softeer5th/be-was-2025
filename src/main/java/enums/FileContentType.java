package enums;

import exception.ClientErrorException;

import static exception.ErrorCode.UNSUPPORTED_FILE_EXTENSION;

/**
 * 파일의 Content-Type을 관리하는 열거형 클래스입니다.
 * 각 파일 확장자와 해당하는 Content-Type을 매핑합니다.
 */
public enum FileContentType {
    /**
     * HTML 파일 (UTF-8 인코딩)
     */
    HTML_UTF_8("text/html; charset=utf-8"),

    /**
     * CSS 파일
     */
    CSS("text/css"),

    /**
     * JavaScript 파일
     */
    JS("text/javascript"),

    /**
     * 아이콘 파일 (.ico)
     */
    ICO("image/x-icon"),

    /**
     * SVG 이미지 파일
     */
    SVG("image/svg+xml"),

    /**
     * PNG 이미지 파일
     */
    PNG("image/png"),

    /**
     * JPG 이미지 파일
     */
    JPG("image/jpeg");

    private final String contentType;

    /**
     * 열거형 인스턴스를 생성합니다.
     *
     * @param contentType 파일의 Content-Type
     */
    FileContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Content-Type 문자열을 반환합니다.
     *
     * @return Content-Type 문자열
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * 파일 경로를 기반으로 파일 확장자에 해당하는 Content-Type 열거형을 반환합니다.
     *
     * @param path 파일 경로 (예: "/path/to/file.html")
     * @return 파일 확장자에 해당하는 {@link FileContentType} 열거형
     * @throws ClientErrorException 지원하지 않는 파일 확장자일 경우 예외를 던집니다.
     */
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
