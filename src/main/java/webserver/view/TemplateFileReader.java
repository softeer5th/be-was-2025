package webserver.view;

// 템플릿 파일을 문자열로 읽어오는 역할을 하는 인터페이스
public interface TemplateFileReader {
    String read(String templateName);
}
