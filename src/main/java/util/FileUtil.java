package util;

import exception.BaseException;
import exception.FileErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileUtil {

    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public static byte[] readHtmlFileAsBytes(String filepath) {
        try {
            File file = new File(filepath);
            if (!file.exists() || file.isDirectory()) {
                logger.error("File not found or is a directory: {}", filepath);
                throw new BaseException(FileErrorCode.FILE_NOT_FOUND);
            }

            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);

            byte[] body = new byte[(int) file.length()];
            bis.read(body);
            return body;
        } catch (IOException e) {
            logger.error("File read error: {}", e.getMessage());
            throw new BaseException(FileErrorCode.FILE_NOT_FOUND);
        }
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

