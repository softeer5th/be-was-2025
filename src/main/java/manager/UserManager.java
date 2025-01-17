package manager;

import Response.HTTPResponse;
import constant.HTTPCode;
import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.HTTPRequest;

import java.security.SecureRandom;
import java.util.Base64;

import static util.Utils.generateSessionID;
import static util.Utils.getSessionIdInCookie;

public class UserManager {

    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);
    private static final String redirectAfterSignUp = "/index.html";
    private static final String redirectAfterLogIn = "/index.html";
    private static final String redirectAfterLogInFail = "/user/login_failed.html";
    public static final String COOKIE = "cookie";


    public HTTPResponse signUp(HTTPRequest httpRequest){
        if(Database.userExists(httpRequest.getBodyParameterByKey("userId"))){
            return HTTPResponse.createFailResponse(httpRequest.getHttpVersion(),HTTPCode.ALREADY_EXIST_USER);
        }

        User user = new User(httpRequest.getBodyParameterByKey("userId")
                ,httpRequest.getBodyParameterByKey("password")
                ,httpRequest.getBodyParameterByKey("name")
                ,httpRequest.getBodyParameterByKey("email"));
        Database.addUser(user);
        logger.debug("signUp : " + user.getUserId());
        return HTTPResponse.createRedirectResponse(httpRequest.getHttpVersion(),HTTPCode.FOUND,redirectAfterSignUp);

    }

    public HTTPResponse logIn(HTTPRequest httpRequest){
        if(!Database.userExists(httpRequest.getBodyParameterByKey("userId"))){
            return HTTPResponse.createRedirectResponse(httpRequest.getHttpVersion(),HTTPCode.SEE_OTHER, redirectAfterLogInFail);
        }

        User user = Database.findUserById(httpRequest.getBodyParameterByKey("userId"));
        if(!user.getPassword().equals(httpRequest.getBodyParameterByKey("password"))){
            return HTTPResponse.createRedirectResponse(httpRequest.getHttpVersion(),HTTPCode.SEE_OTHER, redirectAfterLogInFail);
        }

        String sessionId = generateSessionID();
        Database.addSession(sessionId,user.getUserId());

        return HTTPResponse.createLoginRedirectResponse(httpRequest.getHttpVersion(),HTTPCode.FOUND,redirectAfterLogIn, sessionId);
    }

    public HTTPResponse checkLoginStatus(HTTPRequest httpRequest){
        String sessionId = getSessionIdInCookie(httpRequest.getHeaderByKey(COOKIE));
        if(!Database.sessionExists(sessionId)){
            return HTTPResponse.createFailResponse(httpRequest.getHttpVersion(), HTTPCode.UNAUTHORIZED);
        }
        String userId = Database.getSession(sessionId);
        User user = Database.findUserById(userId);

        return HTTPResponse.createSuccessResponse(httpRequest.getHttpVersion(), HTTPCode.OK, user.getName());
    }

}
