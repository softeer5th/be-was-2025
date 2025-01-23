package webserver.response;


import handler.*;
import webserver.request.Request;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;


public class ResponseBuilder {
    private final static Map<String, Handler> getPages = Map.of(
            "default", new ReadFileHandler()
    );
    private final static Map<String, Handler> postPages = Map.of(
            "/user/create", new CreateUserHandler(),
            "/user/login.html", new LoginHandler(),
            "/user/logout", new LogoutHandler(),
            "/user/update", new UpdatePasswordHandler(),
            "/post/add", new AddPostHandler(),
            "/comment/add", new AddCommentHandler(),
            "/user/profile/modify", new ModifyProfileImageHandler(),
            "/user/profile/reset", new ResetProfileImageHandler(),
            "default", new Page404Handler()
    );

    public void buildResponse(DataOutputStream dos, Request request, String sid) throws IOException {
        Map<String, Handler> pages = switch (request.method) {
            case "GET" -> getPages;
            case "POST" -> postPages;
            default -> getPages;
        };

        Handler handler = pages.getOrDefault(request.getUrl(), pages.get("default"));
        if(sid != null) {handler.setSessionId(sid);}

        Response response = handler.handle(request);
        ResponseWriter.write(dos, response);
    }
}
