package webserver.enumeration;

import webserver.exception.HTTPException;

public enum HTTPVersion {
    HTTP_1_0,
    HTTP_1_1,
    HTTP_2;

    public static HTTPVersion from(String version) {
        if (version == null || version.isEmpty()) {
            throw new HTTPException.Builder()
                    .causedBy(HTTPVersion.class)
                    .badRequest("HTTP version is empty");
        }

        return switch (version) {
            case "HTTP/1.0" -> HTTP_1_0;
            case "HTTP/1.1" -> HTTP_1_1;
            case "HTTP/2" -> HTTP_2;
            default -> throw new HTTPException.Builder()
                    .causedBy(HTTPVersion.class)
                    .badRequest("HTTP version is not supported: " + version);
        };
    }
}
