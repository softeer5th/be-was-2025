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

    public static byte[] readHtmlFileAsBytes(String filepath) {
        try {
            File file = new File(filepath);

            // filepath가 디렉토리인 경우 /index.html을 추가
            if( file.isDirectory()) {
                filepath += "/index.html";
                file = new File(filepath);
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
        } else if( filepath.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "application/octet-stream";
    }
}

