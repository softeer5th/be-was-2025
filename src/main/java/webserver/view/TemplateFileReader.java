package webserver.view;

/**
 * 템플릿 파일을 문자열로 읽어오는 역할을 하는 인터페이스
 */
public interface TemplateFileReader {
    /**
     * 템플릿 파일을 읽어서 문자열로 반환한다.
     *
     * @param templateName 템플릿 파일 이름
     * @return 템플릿 파일 내용
     */
    String read(String templateName);
}
