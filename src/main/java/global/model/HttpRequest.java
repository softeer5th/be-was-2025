package global.model;

public record HttpRequest(String method, String path, String headers, String body) {
}