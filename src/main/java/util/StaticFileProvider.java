package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class StaticFileProvider {
    private static final Logger logger = LoggerFactory.getLogger(StaticFileProvider.class);
    private static final String BASE_DIRECTORY = "./src/main/resources/static/";

    public static File findStaticFileByUrl(String url) {
        String filePath = BASE_DIRECTORY + url;

        File file = new File(filePath);

        if(file.exists()){
            return file;
        }

        return null;
    }

    public static byte[] readStaticFileToByteArray(File file){
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ) {
            byte[] buffer = new byte[1024];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }

            return baos.toByteArray();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return null;
    }
}
