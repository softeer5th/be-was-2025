package request;

public record ImageRequest(
        byte[] fileData,
        String fileExtension
) {
}
