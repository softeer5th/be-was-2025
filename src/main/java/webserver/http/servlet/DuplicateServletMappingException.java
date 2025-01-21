package webserver.http.servlet;

public class DuplicateServletMappingException extends RuntimeException {
    public DuplicateServletMappingException(String message) {
        super(message);
    }
}
