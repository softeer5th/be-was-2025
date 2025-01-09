package util;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    private static final String ROOT_PATH = "./src/main/resources/static";

    public static File findFile(String target) throws IOException {
        return new File(ROOT_PATH + target);
    }

    public static File findFile(File file) {
        return new File(file.getPath() + "/index.html");
    }
}
