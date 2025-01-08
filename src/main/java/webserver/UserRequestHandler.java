package webserver;

import db.Database;
import enums.FileContentType;
import enums.HttpStatus;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import request.UserCreateRequest;
import util.HttpResponse;
import util.RequestInfo;

import static enums.HttpMethod.GET;

public class UserRequestHandler implements Handler {
    private static final Logger logger = LoggerFactory.getLogger(UserRequestHandler.class);

    @Override
    public HttpResponse handle(RequestInfo request)  {
        logger.debug("request : {} ", request);

        String path = request.getPath().substring("/user/".length());
        HttpResponse response = new HttpResponse();

        if (request.getMethod().equals(GET) && path.startsWith("create?")) {
            UserCreateRequest userCreateRequest = UserCreateRequest.of(path.substring("create?".length()));
            createUser(userCreateRequest);

            response.setStatus(HttpStatus.CREATED);
            response.setContentType(FileContentType.HTML);
            response.setBody("Successfully created user");
        }

        return response;
    }

    private void createUser(UserCreateRequest request) {
        User user = new User(request.userId(),
                request.password(),
                request.nickname(),
                request.email());
        Database.addUser(user);
    }
}
