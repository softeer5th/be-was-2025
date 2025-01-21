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

        String sessionId = getSessionIdInCookie(httpRequest.getHeaderByKey(COOKIE));
        if(!SessionDatabase.sessionExists(sessionId)){
            return HTTPResponse.createFailResponse(httpRequest.getHttpVersion(), HTTPCode.UNAUTHORIZED);
        }
        String userId = SessionDatabase.getSession(sessionId);
        Optional<User> optionalUser = Database.findUserById(userId);
        if(optionalUser.isEmpty()){
            return HTTPResponse.createFailResponse(httpRequest.getHttpVersion(), HTTPCode.UNAUTHORIZED);
        }
        User user = optionalUser.get();

        String content = httpRequest.getBodyParameterByKey("content");

        Article article = new Article(user.getUserId(),content);

        Database.addArticle(article);

        return HTTPResponse.createRedirectResponse(httpRequest.getHttpVersion(),HTTPCode.FOUND,redirectAfterCreateArticle);
    }

    public HTTPResponse getAllArticles(HTTPRequest httpRequest){
        return HTTPResponse.createSuccessResponse(httpRequest.getHttpVersion(),HTTPCode.OK, Database.findAllArticles());
    }


}
