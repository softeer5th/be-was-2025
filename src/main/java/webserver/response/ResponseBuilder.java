package webserver.response;


import handler.*;
import webserver.request.Request;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;


public class ResponseBuilder {
    private final static Map<String, Handler> getPages = Map.of(
            "default", new StaticFileHandler()
    );
    private final static Map<String, Handler> postPages = Map.of(
            "/user/create", new CreateUserHandler(),
            "/user/login.html", new LoginHandler(),
            "default", new Page404Handler()
    );

    public void buildResponse(DataOutputStream dos, Request request) throws IOException {
        Map<String, Handler> pages = switch (request.method) {
            case "GET" -> getPages;
            case "POST" -> postPages;
            default -> getPages;
        };

        Response response;
        if (pages.containsKey(request.url)) {
            Handler handler = pages.get(request.url);
            response = handler.handle(request);
        }
        else{
            response = pages.get("default").handle(request);
        }
        ResponseWriter.write(dos, response);
    }
}
