package exception;

public class FileNotSupportedException extends RuntimeException {
    public FileNotSupportedException() {
    }

    public FileNotSupportedException(String message) {
        super(message);
    }
}
