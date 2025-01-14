package manager;

import Response.HTTPResponseHandler;
import constant.HTTPCode;
import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import requst.QueryString;

import java.io.DataOutputStream;

public class UserManager {

    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);
    private static final HTTPResponseHandler httpResponseHandler = new HTTPResponseHandler();


    public void signUp(String query, DataOutputStream dos){
        QueryString queryString = new QueryString(query);

        if(Database.userExists(queryString.getSingleValueByKey("userId"))){
            httpResponseHandler.responseFailHandler(dos, HTTPCode.ALREADY_EXIST_USER);
            return;
        }

        User user = new User(queryString.getSingleValueByKey("userId")
                ,queryString.getSingleValueByKey("password")
                ,queryString.getSingleValueByKey("nickname")
                ,queryString.getSingleValueByKey("email"));
        Database.addUser(user);
        httpResponseHandler.responseRedirectHandler(dos, HTTPCode.FOUND,"/main/index.html");

    }
}
