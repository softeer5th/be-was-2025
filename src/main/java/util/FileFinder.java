package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

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

    public byte[] readFileToBytes() throws IOException {
        File file = new File(path);
        byte[] bytes = new byte[(int) file.length()];

        try(FileInputStream fis = new FileInputStream(file)) {
            fis.read(bytes);
        } catch (IOException e) {
            throw e;
        }
        return bytes;
    }

    public String getPath() {
        return path;
    }

}
