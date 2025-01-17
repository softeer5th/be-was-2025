package webserver.support;

import java.io.File;
import java.net.URL;

public class ResourceResolver {
    private static final String STATIC = "static";

    public static File getResource(String path) {
        return readResource(STATIC + path);
    }


    private static File readResource(String resourcePath) {
        ClassLoader classLoader = ResourceResolver.class.getClassLoader();
        URL resourceUrl = classLoader.getResource(resourcePath);
        if (resourceUrl == null) {
            return new File(resourcePath);
        }

        return new File(resourceUrl.getFile());
    }
}

