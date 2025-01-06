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
        File baseDir = new File(BASE_DIRECTORY);
        File[] files = baseDir.listFiles();

        if (files == null) {
            return null;
        }

        for (File file : files) {
            File resultFile = findStaticFileByUrlInDirectory(file, baseDir + url);
            if (resultFile != null) {
                return resultFile;
            }
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

    private static File findStaticFileByUrlInDirectory(File file, String url) {
        if(file.isDirectory()){
            File[] subFiles = file.listFiles();

            if(subFiles == null || subFiles.length == 0){
                return null;
            }

            for (File subFile: subFiles){
                File resultPath = findStaticFileByUrlInDirectory(subFile, url);

                if(resultPath != null){
                    return resultPath;
                }
            }
        }

        if (file.getPath().equals(url)) {
            return file;
        }
        return null;
    }
}
