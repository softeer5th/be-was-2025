package manager;

import Response.HTTPResponseHandler;
import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;

public class UserManager {

    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);
    private static final HTTPResponseHandler httpResponseHandler = new HTTPResponseHandler();


    public void signUp(String query, DataOutputStream dos){
        String queryString = query.substring(query.indexOf("?") + 1);

        String[] pairs = queryString.split("&");
        String userId = "", password = "", name = "", email = "";
        try {
            for (String pair : pairs) {
                String[] keyValue = pair.split("=", 2);
                String key = keyValue[0];
                String value = java.net.URLDecoder.decode(keyValue[1], "UTF-8");

                switch (key) {
                    case "userId":
                        userId = value;
                        break;
                    case "password":
                        password = value;
                        break;
                    case "nickname":
                        name = value;
                        break;
                    case "email":
                        email = value;
                        break;
                }
            }

            User user = new User(userId, password, name, email);
            Database.addUser(user);
            httpResponseHandler.responseRedirectHandler(dos, "/main/index.html");

        }
        catch (Exception e){
            logger.error(e.getMessage());
        }
    }
}
