package webserver.writer.html.template;

import webserver.exception.HTTPException;
import webserver.reader.TemplateHTMLReader;

import java.io.IOException;
import java.io.InputStream;

public class MyPageWriter {
    private static final String TEMPLATE_NAME = "/static/mypage/index.html";

    public static String write(String sid) {
        StringBuilder content = new StringBuilder();
        try (
                InputStream in = IndexPageWriter.class.getResourceAsStream(TEMPLATE_NAME);
                TemplateHTMLReader reader = new TemplateHTMLReader(in)
        ) {
            content.append(reader.readUntil('$'));
            return content.toString();
        } catch (IOException e) {
            throw new HTTPException.Builder()
                    .causedBy(IndexPageWriter.class)
                    .internalServerError(e.getMessage());
        }
    }
}
