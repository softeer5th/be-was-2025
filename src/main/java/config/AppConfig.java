package config;

import db.LocalUserDataManager;
import db.SessionDataManager;
import db.UserDataManager;
import db.LocalSessionDataManager;
import handler.*;

public class AppConfig {
    private static final UserDataManager userDataManager = LocalUserDataManager.getInstance();
    private static final SessionDataManager sessionDataManager = LocalSessionDataManager.getInstance();


    private static final UserRegisterHandler userRegisterHandler = new UserRegisterHandler(userDataManager);
    private static final UserLoginHandler userLoginHandler = new UserLoginHandler(userDataManager, sessionDataManager);
    private static final UserLogoutHandler userLogoutHandler = new UserLogoutHandler(sessionDataManager);
    private static final FileRequestHandler fileRequestHandler = new FileRequestHandler(userDataManager, sessionDataManager);

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
}
