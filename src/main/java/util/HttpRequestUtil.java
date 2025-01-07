package util;

public class HttpRequestUtil {

    public static String getUrl(String inputString) {
        if (inputString == null) {
            return null;
        }
        String[] tokens = inputString.split(" "); // 첫 줄을 split을 통해 분할
        if (tokens.length != 3) {
            return null;
        } else if (!tokens[1].startsWith("/")) {
            return null;
        }
        return tokens[1];
    }

    public static boolean isDirectory(String inputString) {
        return inputString != null && !inputString.contains(".");
    }

    public static String getType(String inputString) {
        String type = inputString.split("\\.")[2];
        String typeString = ContentType.getMimeTypeByExtension(type);
        if (typeString == null) {
            typeString = "text/html";
        }

        return typeString;
    }
}
