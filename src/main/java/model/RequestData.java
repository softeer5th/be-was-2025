package model;

public record RequestData(String method, String path, String headers, String body) {
}