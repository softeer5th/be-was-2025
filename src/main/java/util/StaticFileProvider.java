package util;

import java.io.File;

public class StaticFileProvider {
    private static final String BASE_DIRECTORY = "./src/main/resources/static/";

    public static File findStaticFileByUrl(String url){
        File baseDir = new File(BASE_DIRECTORY);
        File[] files = baseDir.listFiles();

        if(files == null){
            return null;
        }

        for(File file: files){
            File resultFile = findStaticFileByUrlInDirectory(file, baseDir + url);
            if (resultFile != null) {
                return resultFile;
            }
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
