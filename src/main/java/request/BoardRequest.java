package request;

public record BoardRequest(
        String content,
        byte[] fileData,
        String fileExtension
) {
}
