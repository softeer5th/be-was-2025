package webserver.http.servlet.exception;

public class DuplicateServletMappingException extends ServletException {
    public DuplicateServletMappingException(String message) {
        super(message);
    }
}
