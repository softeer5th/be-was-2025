package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.RequestHandler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

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

}

