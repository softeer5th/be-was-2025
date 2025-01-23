package manager;

import db.UserDatabase;
import response.HTTPResponse;
import constant.HTTPCode;
import db.SessionDatabase;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.HTTPRequest;

import java.util.Optional;

import static util.Utils.generateSessionID;
import static util.Utils.getSessionIdInCookie;

public class UserManager {

    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);
    private static final String redirectAfterSignUp = "/index.html";
    private static final String redirectAfterLogIn = "/index.html";
    private static final String redirectAfterLogInFail = "/user/login_failed.html";
    public static final String COOKIE = "cookie";


    public HTTPResponse signUp(HTTPRequest httpRequest){
        if(UserDatabase.userExists(httpRequest.getBodyParameter("userId"))){
            return HTTPResponse.createFailResponse(httpRequest.getHttpVersion(),HTTPCode.ALREADY_EXIST_USER);
        }

        User user = new User(httpRequest.getBodyParameter("userId")
                ,httpRequest.getBodyParameter("password")
                ,httpRequest.getBodyParameter("name")
                ,httpRequest.getBodyParameter("email"));
        UserDatabase.addUser(user);
        logger.debug("signUp : " + user.getUserId());
        return HTTPResponse.createRedirectResponse(httpRequest.getHttpVersion(),HTTPCode.FOUND,redirectAfterSignUp);

    }

    public HTTPResponse logIn(HTTPRequest httpRequest){

        Optional<User> optionalUser = UserDatabase.findUserById(httpRequest.getBodyParameter("userId"));
        if(optionalUser.isEmpty()){
            return HTTPResponse.createRedirectResponse(httpRequest.getHttpVersion(),HTTPCode.SEE_OTHER, redirectAfterLogInFail);
        }

        User user = optionalUser.get();
        if(!user.getPassword().equals(httpRequest.getBodyParameter("password"))){
            return HTTPResponse.createRedirectResponse(httpRequest.getHttpVersion(),HTTPCode.SEE_OTHER, redirectAfterLogInFail);
        }

        String sessionId = generateSessionID();
        SessionDatabase.addSession(sessionId,user.getUserId());

        return HTTPResponse.createLoginRedirectResponse(httpRequest.getHttpVersion(),HTTPCode.FOUND,redirectAfterLogIn, sessionId);
    }

    public HTTPResponse checkLoginStatus(HTTPRequest httpRequest){
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
        Optional<User> optionalUser = UserDatabase.findUserById(userId);
        if(optionalUser.isEmpty()){
            return HTTPResponse.createFailResponse(httpRequest.getHttpVersion(), HTTPCode.UNAUTHORIZED);
        }
        User user = optionalUser.get();

        return HTTPResponse.createSuccessResponse(httpRequest.getHttpVersion(), HTTPCode.OK, user.getName());
    }

}
