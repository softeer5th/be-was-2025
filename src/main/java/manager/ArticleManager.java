package manager;


import constant.HTTPCode;
import db.Database;
import db.SessionDatabase;
import model.Article;
import model.User;
import request.HTTPRequest;
import response.HTTPResponse;

import java.util.Optional;

import static manager.UserManager.COOKIE;
import static util.Utils.getSessionIdInCookie;

public class ArticleManager {

    private final static String redirectAfterCreateArticle = "/index.html";

    public HTTPResponse createArticle(HTTPRequest httpRequest){

        Optional<String> cookie = httpRequest.getHeader(COOKIE);
        if(cookie.isEmpty()){
            return HTTPResponse.createFailResponse(httpRequest.getHttpVersion(), HTTPCode.UNAUTHORIZED);
        }
        String sessionId = getSessionIdInCookie(cookie.get());
        if(!SessionDatabase.sessionExists(sessionId)){
            return HTTPResponse.createFailResponse(httpRequest.getHttpVersion(), HTTPCode.UNAUTHORIZED);
        }
        Optional<String> optionalUserId = SessionDatabase.getSession(sessionId);
        if(optionalUserId.isEmpty()){
            return HTTPResponse.createFailResponse(httpRequest.getHttpVersion(), HTTPCode.UNAUTHORIZED);
        }
        String userId = optionalUserId.get();
        Optional<User> optionalUser = Database.findUserById(userId);
        if(optionalUser.isEmpty()){
            return HTTPResponse.createFailResponse(httpRequest.getHttpVersion(), HTTPCode.UNAUTHORIZED);
        }
        User user = optionalUser.get();

        String content = httpRequest.getBodyParameter("content");

        Article article = new Article(user.getUserId(),content);

        Database.addArticle(article);

        return HTTPResponse.createRedirectResponse(httpRequest.getHttpVersion(),HTTPCode.FOUND,redirectAfterCreateArticle);
    }

    public HTTPResponse getAllArticles(HTTPRequest httpRequest){
        return HTTPResponse.createSuccessResponse(httpRequest.getHttpVersion(),HTTPCode.OK, Database.findAllArticles());
    }


}
