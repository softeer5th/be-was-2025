package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUtils {
    private static final String ROOT_PATH = "./src/main/resources/static";

    public static File findFile(String target) throws IOException {
        File file = new File(ROOT_PATH + target);
        if (file.exists()) {
            if (file.isDirectory()) {
                file = new File(ROOT_PATH + target + "/index.html");
            }
        } else {
            throw new FileNotFoundException();
        }

        return file;
    }
}
