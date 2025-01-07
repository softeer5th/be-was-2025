package webserver.response.body;

import webserver.enums.ContentType;

import java.io.File;
import java.io.OutputStream;
import java.util.Optional;

public abstract class Body {
    public static Body empty() {
        return new EmptyBody();
    }

    public static Body of(String string) {
        return new StringBody(string);
    }

    public static Body of(File file) {
        return new FileBody(file);
    }

    public abstract void writeBody(OutputStream out);

    public abstract Long getContentLength();

    public abstract Optional<ContentType> getContentType();
}
