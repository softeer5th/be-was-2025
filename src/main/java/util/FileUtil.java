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

    public static File findFileByPath(String path) {
        String filePath = BASE_DIRECTORY + path;

        File file = new File(filePath);

        // 해당 파일 객체가 디렉토리라면 해당 디렉토리의 index.html 파일을 보여준다.
        if(file.isDirectory()){
            filePath += "/index.html";
            return new File(filePath);
        }

        // 파일 객체가 존재하는 지만 확인하는 것이 아닌 해당 파일 객체가 디렉토리인지 확인하는 과정이 필요하다.
        if(file.exists()){
            return file;
        }

        return null;
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
