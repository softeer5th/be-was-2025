package util;

import java.io.File;

public class FileFinder {
    private static final String STATICPATH = "./src/main/resources/static";
    private String url;

    public FileFinder(String url) {
        this.url = url;
    }

    public String getPath() {
        String path = STATICPATH + url;
        File file = new File(path);
        if (file.exists()) {
            if(file.isDirectory()) {
                path += "/index.html";
            }
            return path;
        }
        return null;
    }

}
