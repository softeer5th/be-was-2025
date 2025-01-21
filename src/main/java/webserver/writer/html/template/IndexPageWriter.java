package webserver.writer.html.template;

import webserver.exception.HTTPException;
import webserver.reader.TemplateHTMLReader;
import webserver.writer.html.HTMLElement;
import webserver.writer.html.HTMLElement.Builder;
import webserver.writer.html.HTMLTag;
import webserver.writer.html.HTMLWriter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class IndexPageWriter {
    private static final String TEMPLATE_NAME = "/index.html";
    private static String postAccountName(Optional<String> nickname) {
         HTMLElement element = Builder.tag(HTMLTag.P)
                .className("post__account_nickname")
                 .appendChild(Builder.value(nickname.orElse("account")))
                .build();
         return HTMLWriter.render(element);
    }

    public static String write(Optional<String> username) {
        StringBuilder content = new StringBuilder();
        try (
                InputStream in = IndexPageWriter.class.getResourceAsStream(TEMPLATE_NAME);
                TemplateHTMLReader reader = new TemplateHTMLReader(in)
        ) {
            // TODO:: ${:key} key를 가지고 자동으로 뿌려주는 함수와 매칭
            content.append(reader.readUntil('$'));
            content.append("\n");
            content.append(postAccountName(username));
            content.append(reader.readUntil('$'));
            return content.toString();
        } catch (IOException e) {
            throw new HTTPException.Builder()
                    .causedBy(IndexPageWriter.class)
                    .internalServerError(e.getMessage());
        }
    }
}
