package webserver.http.servlet;

public class DuplicateServletMappingException extends RuntimeException {
    public DuplicateServletMappingException() {super("Duplicate servlet mapping");}

    public DuplicateServletMappingException(String message) {
        super(message);
    }
}
