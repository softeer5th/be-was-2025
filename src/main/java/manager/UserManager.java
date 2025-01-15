package manager;

import Response.HTTPResponse;
import Response.HTTPResponseHandler;
import constant.HTTPCode;
import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.HTTPRequest;

public class UserManager {

    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);
    private static final HTTPResponseHandler httpResponseHandler = new HTTPResponseHandler();
    private static final String redirectAfterSignUp = "/index.html";


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
}
