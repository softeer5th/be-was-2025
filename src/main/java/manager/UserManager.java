package manager;

import Response.HTTPResponse;
import Response.HTTPResponseHandler;
import constant.HTTPCode;
import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.HTTPRequest;

import java.security.SecureRandom;
import java.util.Base64;

public class UserManager {

    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);
    private static final HTTPResponseHandler httpResponseHandler = new HTTPResponseHandler();
    private static final String redirectAfterSignUp = "/index.html";
    private static final String redirectAfterLogIn = "/index.html";
    private static final int SESSION_ID_LENGTH = 32;


    public HTTPResponse signUp(HTTPRequest httpRequest){
        if(Database.userExists(httpRequest.getBodyParameterByKey("userId"))){
            return HTTPResponse.createFailResponse(httpRequest.getHttpVersion(),HTTPCode.ALREADY_EXIST_USER);
        }

        User user = new User(httpRequest.getBodyParameterByKey("userId")
                ,httpRequest.getBodyParameterByKey("password")
                ,httpRequest.getBodyParameterByKey("nickname")
                ,httpRequest.getBodyParameterByKey("email"));
        Database.addUser(user);
        logger.debug("signUp : " + user.getUserId());
        return HTTPResponse.createRedirectResponse(httpRequest.getHttpVersion(),HTTPCode.FOUND,redirectAfterSignUp);

    }

    public HTTPResponse logIn(HTTPRequest httpRequest){
        if(!Database.userExists(httpRequest.getBodyParameterByKey("userId"))){
            return HTTPResponse.createFailResponse(httpRequest.getHttpVersion(),HTTPCode.UNAUTHORIZED);
        }

        User user = Database.findUserById(httpRequest.getBodyParameterByKey("userId"));
        if(!user.getPassword().equals(httpRequest.getBodyParameterByKey("password"))){
            return HTTPResponse.createFailResponse(httpRequest.getHttpVersion(),HTTPCode.UNAUTHORIZED);
        }

        String sessionId = generateSessionID();
        Database.addSession(sessionId,user.getUserId());

        return HTTPResponse.createLoginRedirectResponse(httpRequest.getHttpVersion(),HTTPCode.FOUND,redirectAfterLogIn, sessionId);
    }

    public static String generateSessionID() {
        SecureRandom secureRandom = new SecureRandom(); // 암호학적으로 안전한 랜덤 생성기
        byte[] randomBytes = new byte[SESSION_ID_LENGTH];
        secureRandom.nextBytes(randomBytes); // 랜덤 바이트 생성
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes); // URL-safe Base64로 인코딩
    }
}
