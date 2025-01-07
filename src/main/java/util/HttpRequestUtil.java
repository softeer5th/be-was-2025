package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class HttpRequestUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestUtil.class);

    private static final String DEFAULT_STATIC_RESOURCE_FILE = "index.html";

    private HttpRequestUtil() {}

    public static String getUrl(String inputString) {
        if (inputString == null) {
            return null;
        }
        String[] tokens = inputString.split(" +"); // 첫 줄을 split을 통해 분할
        if (tokens.length < 3) {
            return null;
        } else if (!tokens[1].startsWith("/")) {
            return null;
        }
        return tokens[1];
    }

    public static String getType(String inputString) {
        String[] token = inputString.split("\\.");
        if (token.length < 3) {
            return ContentType.DEFAULT.getMimeType();
        } else {
            String type = token[2];
            String typeString = ContentType.getMimeTypeByExtension(type);
            if (typeString == null) {
                typeString = ContentType.DEFAULT.getMimeType();
            }
            return typeString;
        }
    }

    public static String buildPath(String path) {
        logger.debug(path);
        File file = new File(path);
        if (file.isDirectory()) {
            if (!path.endsWith("/")) path += "/";
            path += DEFAULT_STATIC_RESOURCE_FILE;
        }
        return path;
    }
}
