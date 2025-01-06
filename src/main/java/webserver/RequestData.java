package webserver;

public record RequestData(String method, String path, String body) {
}