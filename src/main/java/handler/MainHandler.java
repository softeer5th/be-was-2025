package handler;

import db.ArticleStore;
import db.Database;
import http.HttpRequest;
import http.HttpResponse;
import http.constant.HttpStatus;
import model.Article;
import model.Session;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.DynamicHtmlEditor;
import util.FileUtils;
import util.MimeType;
import util.SessionUtils;
import util.exception.UserNotFoundException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class MainHandler implements Handler{
    private static final Logger logger = LoggerFactory.getLogger(MainHandler.class);

    private static final String DEFAULT_PAGE = "0";

    @Override
    public void handle(HttpRequest httpRequest, HttpResponse httpResponse) throws IOException {
        if (!SessionUtils.isLogin(httpRequest)) {
            httpResponse.redirect("/");
            return;
        }
        String path = httpRequest.getPath().toLowerCase();

        File file = FileUtils.findFile(path);
        String content = FileUtils.convertToString(file);
        String extension = FileUtils.getExtension(file);
        MimeType mimeType = MimeType.valueOf(extension.toUpperCase());


        Session session = SessionUtils.findSession(httpRequest);
        User user = Database.findUserById(session.userId())
                .orElseThrow(() -> new UserNotFoundException("해당 사용자가 없습니다."));

        String page = httpRequest.getQueries().getOrDefault("page", DEFAULT_PAGE);

        List<Article> articles = ArticleStore.findAll();
        int articleNum = articles.size();
        int pageNum = Integer.parseInt(page);
        Article article;
        if (articles.isEmpty()) {
            article = defaultArticle();
        }
        else {
            article = articles.get(pageNum);
        }

        int prevNum = Math.max(pageNum - 1, 0);
        int nextNum = Math.min(pageNum + 1, articleNum - 1);

        content = DynamicHtmlEditor.edit(content, "username", user.getName());
        content = DynamicHtmlEditor.edit(content, "author", article.getUser().getName());
        content = DynamicHtmlEditor.edit(content, "content", article.getContent());
        content = DynamicHtmlEditor.edit(content, "prevPage", String.valueOf(prevNum));
        content = DynamicHtmlEditor.edit(content, "nextPage", String.valueOf(nextNum));

        byte[] body = content.getBytes();
        httpResponse.writeStatusLine(HttpStatus.OK);
        httpResponse.writeBody(body, mimeType.getMimeType());
        httpResponse.send();
    }

    private Article defaultArticle() {
        String content = """
                우리는 시스템 아키텍처에 대한 일관성 있는 접근이 필요하며, 필요한
                            모든 측면은 이미 개별적으로 인식되고 있다고 생각합니다. 즉, 응답이
                            잘 되고, 탄력적이며 유연하고 메시지 기반으로 동작하는 시스템 입니다.
                            우리는 이것을 리액티브 시스템(Reactive Systems)라고 부릅니다.
                            리액티브 시스템으로 구축된 시스템은 보다 유연하고, 느슨한 결합을
                            갖고, 확장성 이 있습니다. 이로 인해 개발이 더 쉬워지고 변경 사항을
                            적용하기 쉬워집니다. 이 시스템은 장애 에 대해 더 강한 내성을 지니며,
                            비록 장애가 발생 하더라도, 재난이 일어나기 보다는 간결한 방식으로
                            해결합니다. 리액티브 시스템은 높은 응답성을 가지며 사용자 에게
                            효과적인 상호적 피드백을 제공합니다.
                """;
        User user = new User("0", "1", "account", null);
        Article article = new Article(content, user);
        ArticleStore.addArticle(article);
        return article;
    }
}
