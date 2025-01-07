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

    public static byte[] fileToByteArray(File file) {
        try {
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

