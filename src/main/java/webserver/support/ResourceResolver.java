package webserver.support;

import java.io.File;
import java.io.IOException;
import java.net.URL;

public class ResourceResolver {
    private static final String STATIC = "static";
    private static final String INDEX_FILE = "index.html";

    public static File getResource(String path) throws IOException {
        File resourceFile = readResource(STATIC + path);

        if (resourceFile.isDirectory()) {
            resourceFile = new File(resourceFile, INDEX_FILE);
        }

        return resourceFile;
    }


    private static File readResource(String resourcePath) throws IOException {
        ClassLoader classLoader = ResourceResolver.class.getClassLoader();
        URL resourceUrl = classLoader.getResource(resourcePath);
        if (resourceUrl == null) {
            throw new IOException("Resource not found: " + resourcePath);
        }

        return new File(resourceUrl.getFile());
    }
}

