package webserver.load;

public record LoadResult(byte[] content, String path, String contentType) {
}
