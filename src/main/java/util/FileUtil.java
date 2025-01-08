package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    public static String getFilePath(String path) {
        // URL에 점(.)이 있으면 확장자가 있는 파일로 간주하여 그대로 사용
        if (path.contains(".")) {
            return path;
        }

        // 점(.)이 없다면 디렉토리로 간주하고 /index.html을 붙임
        return path + "/index.html";
    }

    public static byte[] readHtmlFileAsBytes(String filepath) {
        try {
            File file = new File(filepath);
            if (!file.exists() || file.isDirectory()) {
                logger.error("File not found or is a directory: {}", filepath);
                return null;
            }

            FileInputStream fis = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(fis);

            byte[] body = new byte[(int) file.length()];
            bis.read(body);
            return body;
        } catch (IOException e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    public static String getContentType(String filepath) {
        if (filepath.endsWith(".html")) {
            return "text/html";
        } else if (filepath.endsWith(".css")) {
            return "text/css";
        } else if (filepath.endsWith(".js")) {
            return "application/javascript";
        } else if (filepath.endsWith(".jpg")) {
            return "image/jpeg";
        } else if (filepath.endsWith(".png")) {
            return "image/png";
        } else if (filepath.endsWith(".ico")) {
            return "image/x-icon";
        } else if (filepath.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "text/html";
    }
}

