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
    private static final String TEMPLATE_NAME = "/static/index.html";
    private static String postAccountName(Optional<String> nickname) {
         HTMLElement element = Builder.tag(HTMLTag.P)
                .className("post__account_nickname")
                 .appendChild(Builder.value(nickname.orElse("account")))
                .build();
         return HTMLWriter.render(element);
    }
    private static String loginOrMyPage(Optional<String> nickname) {
        String href = nickname.isPresent() ? "/mypage" : "/login";
        String childValue = nickname.orElse("로그인");
        HTMLElement child = Builder.value(childValue);
        HTMLElement element = Builder.tag(HTMLTag.A)
                .className("btn btn_contained btn_size_s")
                .href(href)
                .appendChild(child)
                .build();
        return HTMLWriter.render(element);
    }

    public static String write(Optional<String> username) {
        StringBuilder content = new StringBuilder();
        try (
                InputStream in = IndexPageWriter.class.getResourceAsStream(TEMPLATE_NAME);
                TemplateHTMLReader reader = new TemplateHTMLReader(in)
        ) {
            content.append(reader.readUntil('$'));
            content.append("\n");
            reader.readBraceValue();
            content.append(loginOrMyPage(username));
            content.append("\n");
            content.append(reader.readUntil('$'));
            reader.readBraceValue();
            content.append(postAccountName(username));
            content.append(reader.readUntil('$'));
            System.out.println(content);
            return content.toString();
        } catch (IOException e) {
            throw new HTTPException.Builder()
                    .causedBy(IndexPageWriter.class)
                    .internalServerError(e.getMessage());
        }
    }
}
