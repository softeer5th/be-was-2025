package util;

import util.exception.NoSuchPathException;

import java.io.File;
import java.io.IOException;

public class FileUtils {
    private static final String ROOT_PATH = "./src/main/resources/static";

    public static File findFile(String target) throws IOException {
        File file = new File(ROOT_PATH + target);

        return isAvailable(file);
    }

    private static File findFile(File file) {
        return new File(file.getPath() + "/index.html");
    }

    private static File isAvailable(File file) {
        if (file.isDirectory()) {
            file = findFile(file);
        }
        if (!file.exists()) {
            throw new NoSuchPathException();
        }

        return file;
    }
}
