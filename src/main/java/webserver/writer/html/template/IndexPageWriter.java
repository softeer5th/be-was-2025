package webserver.writer.html.template;

import model.Post;
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
    private static final String [] menuImgSources = {"./img/like.svg", "./img/sendLink.svg"};
    private static final String BOOKMARK_IMG_SOURCE = "./img/bookmark.svg";

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


    private static String posts(Optional<Post> post, Optional<String> nickname) {
        if (post.isEmpty()) { return "";}
        Post postValue = post.get();

        HTMLElement accountImg = Builder.tag(HTMLTag.IMG)
            .className("post__account__img")
            .build();
        HTMLElement account = Builder.tag(HTMLTag.DIV)
                .className("post__account")
                .appendChild(accountImg)
                .appendChild(HTMLElement.Builder.value(postAccountName(nickname)))
                .build();
        HTMLElement postImg = Builder.tag(HTMLTag.IMG).className("post__img").build();
        HTMLElement.Builder menuItems = Builder.tag(HTMLTag.UL)
                .className("post__menu__personal");
        for (String imgSource : menuImgSources) {
            HTMLElement button = Builder.tag(HTMLTag.BUTTON)
                    .className("post__menu__btn")
                    .appendChild(Builder.tag(HTMLTag.IMG).src(imgSource).build())
                    .build();
            HTMLElement listItem = Builder.tag(HTMLTag.LI)
                    .appendChild(button)
                    .build();
            menuItems.appendChild(listItem);
        }
        HTMLElement bookMarkButton = Builder.tag(HTMLTag.BUTTON)
                .className("post__menu__btn")
                .appendChild(Builder.tag(HTMLTag.IMG).src(BOOKMARK_IMG_SOURCE).build())
                .build();
        HTMLElement menu = Builder.tag(HTMLTag.DIV)
                .className("post__menu")
                .appendChild(menuItems.build())
                .appendChild(bookMarkButton)
                .build();
        HTMLElement article = Builder.tag(HTMLTag.P)
                .className("post__article")
                .appendChild(Builder.value(postValue.getBody()))
                .build();

        HTMLElement postElement = Builder.tag(HTMLTag.DIV)
                .className("post")
                .appendChild(account)
                .appendChild(postImg)
                .appendChild(menu)
                .appendChild(article)
                .build();
        return HTMLWriter.render(postElement);
    }

    public static String write(Optional<String> userId, Optional<Post> post) {
        StringBuilder content = new StringBuilder();
        try (
                InputStream in = IndexPageWriter.class.getResourceAsStream(TEMPLATE_NAME);
                TemplateHTMLReader reader = new TemplateHTMLReader(in)
        ) {
            content.append(reader.readUntil('$'));
            content.append("\n");
            reader.readBraceValue();
            content.append(loginOrMyPage(userId));
            content.append("\n");
            content.append(reader.readUntil('$'));
            reader.readBraceValue();
            content.append((posts(post, userId)));
            content.append(reader.readUntil('$'));
            reader.readBraceValue();
            content.append(postAccountName(userId));
            content.append(reader.readUntil('$'));
            return content.toString();
        } catch (IOException e) {
            throw new HTTPException.Builder()
                    .causedBy(IndexPageWriter.class)
                    .internalServerError(e.getMessage());
        }
    }
}
