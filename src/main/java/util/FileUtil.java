package util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileUtil {
    private static final Logger logger = LoggerFactory.getLogger(FileUtil.class);
    private static final String BASE_DIRECTORY = "./src/main/resources/static";

    public static boolean isFileExist(String path) {
        String filePath = BASE_DIRECTORY + path;

        File file = new File(filePath);

        if(file.exists()){
            if(file.isFile()){
                return true;
            }
            file = new File(filePath + "/index.html");

            if(file.exists()){
                return true;
            }
        }

        return false;
    }

    public static File getFile(String path){
        String filePath = BASE_DIRECTORY + path;
        File file = new File(filePath);

        // 해당 파일 객체가 디렉토리라면 해당 디렉토리의 index.html 파일을 보여준다.
        if(file.isDirectory()){
            return new File(filePath + "/index.html");
        }

        return new File(filePath);
    }

    public static byte[] readFileToByteArray(File file){
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ) {
            byte[] buffer = new byte[8192];
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

    public static String extractFileExtension(String path){
        String[] pathParts = path.split("\\.");
        return pathParts[pathParts.length - 1];
    }
}
