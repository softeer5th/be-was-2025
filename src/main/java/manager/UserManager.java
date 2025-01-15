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


    public HTTPResponse signUp(HTTPRequest httpRequest){
        if(Database.userExists(httpRequest.getSingleQueryStringByKey("userId"))){
            return HTTPResponse.createFailResponse(httpRequest.getHttpVersion(),HTTPCode.ALREADY_EXIST_USER);
        }

        User user = new User(httpRequest.getSingleQueryStringByKey("userId")
                ,httpRequest.getSingleQueryStringByKey("password")
                ,httpRequest.getSingleQueryStringByKey("nickname")
                ,httpRequest.getSingleQueryStringByKey("email"));
        Database.addUser(user);

        return HTTPResponse.createRedirectResponse(httpRequest.getHttpVersion(),HTTPCode.FOUND,"main/index.html");

    }
}
