package manager;


import constant.HTTPCode;
import db.Database;
import db.InMemoryDatabase;
import model.Article;
import model.User;
import request.HTTPRequest;
import response.HTTPResponse;

import java.util.Optional;
import java.util.UUID;

import static manager.UserManager.COOKIE;
import static util.Utils.getSessionIdInCookie;

public class ArticleManager {

    private final static String redirectAfterCreateArticle = "/index.html";

    public HTTPResponse createArticle(HTTPRequest httpRequest){

        String sessionId = getSessionIdInCookie(httpRequest.getHeaderByKey(COOKIE));
        if(!InMemoryDatabase.sessionExists(sessionId)){
            return HTTPResponse.createFailResponse(httpRequest.getHttpVersion(), HTTPCode.UNAUTHORIZED);
        }
        String userId = InMemoryDatabase.getSession(sessionId);
        Optional<User> optionalUser = Database.findUserById(userId);
        if(optionalUser.isEmpty()){
            return HTTPResponse.createFailResponse(httpRequest.getHttpVersion(), HTTPCode.UNAUTHORIZED);
        }
        User user = optionalUser.get();

        String content = httpRequest.getBodyParameterByKey("content");

        Article article = new Article(UUID.randomUUID().toString(), user.getUserId(),content);

        Database.addArticle(article);

        return HTTPResponse.createRedirectResponse(httpRequest.getHttpVersion(),HTTPCode.FOUND,redirectAfterCreateArticle);
    }


}
