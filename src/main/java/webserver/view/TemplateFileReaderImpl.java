package webserver.view;

import webserver.file.StaticResourceManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TemplateFileReaderImpl implements TemplateFileReader {
    private final String templateExtension;
    private final StaticResourceManager resourceManager;

    public TemplateFileReaderImpl(StaticResourceManager resourceManager, String templateExtension) {
        this.resourceManager = resourceManager;
        this.templateExtension = templateExtension;
    }

    public String read(String templateName) {
        File templateFile = resourceManager.getFile(templateName + templateExtension).orElseThrow(IllegalArgumentException::new);
        try (InputStream in = new FileInputStream(templateFile)) {
            return new String(in.readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
