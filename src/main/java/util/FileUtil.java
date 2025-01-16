package util;

import exception.BaseException;
import exception.FileErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

    public static String readHtmlFileAsString(String filepath) {
        StringBuilder content = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n"); // UTF-8 인코딩 유지
            }
        } catch (FileNotFoundException e) {
            throw new BaseException(FileErrorCode.FILE_NOT_FOUND);
        } catch (IOException e) {
            logger.error("File read error: {}", e.getMessage());
            throw new BaseException(FileErrorCode.FILE_READ_ERROR);
        }

        return content.toString();
    }

    public static String getContentType(String path) {
        int dotIndex = path.toLowerCase().lastIndexOf('.');
        String extension = path.substring(dotIndex + 1);
        return switch (extension) {
            case "html" -> "text/html; charset=utf-8";
            case "css" -> "text/css";
            case "js" -> "application/javascript";
            case "ico" -> "image/x-icon";
            case "jpg" -> "image/jpeg";
            case "svg" -> "image/svg+xml";
            case "png" -> "image/png";
            default -> "application/octet-stream";
        };
    }
}

