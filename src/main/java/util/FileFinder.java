package util;

import java.io.File;

public class FileFinder {
    private static final String STATICPATH = "./src/main/resources/static";
    private final String url;
    private String path;

    public FileFinder(String url) {
        this.url = url;
    }

    public boolean find() {
        this.path = STATICPATH + url;
        File file = new File(path);
        if (file.exists()) {
            if(file.isDirectory()) {
                this.path += "/index.html";
            }
            return true;
        }
        return false;
    }

    public String getPath() {
        return path;
    }

}
