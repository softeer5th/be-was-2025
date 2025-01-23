package config;

import db.*;
import handler.*;

public class AppConfig {
    private static final UserDataManager userDataManager = new JdbcUserDataManager();
    private static final SessionDataManager sessionDataManager = new JdbcSessionDataManager();
    private static final ArticleDataManger articleDataManger = new JdbcArticleDataManager();

    private static final UserRegisterHandler userRegisterHandler = new UserRegisterHandler(userDataManager);
    private static final UserLoginHandler userLoginHandler = new UserLoginHandler(userDataManager, sessionDataManager);
    private static final UserLogoutHandler userLogoutHandler = new UserLogoutHandler(sessionDataManager);
    private static final ArticleCreateHandler articleCreateHandler = new ArticleCreateHandler(articleDataManger, sessionDataManager);
    private static final FileRequestHandler fileRequestHandler = new FileRequestHandler(userDataManager, sessionDataManager, articleDataManger);

    public static UserRegisterHandler getUserRegisterHandler() {
        return userRegisterHandler;
    }

    public static UserLoginHandler getUserLoginHandler() {
        return userLoginHandler;
    }

    public static UserLogoutHandler getUserLogoutHandler() {
        return userLogoutHandler;
    }

    public static FileRequestHandler getFileRequestHandler() {
        return fileRequestHandler;
    }

    public static UserDataManager getUserDataManager() {
        return userDataManager;
    }

    public static SessionDataManager getSessionDataManager() {
        return sessionDataManager;
    }

    public static ArticleCreateHandler getArticleHandler() {
        return articleCreateHandler;
    }
}
