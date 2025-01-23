package webserver.request;

public record FileBody(String fieldName, String fileName, String contentType, byte[] data) {
}
