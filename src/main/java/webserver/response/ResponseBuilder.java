package webserver.response;


import handler.CreateUserHandler;
import handler.Handler;
import handler.Page404Handler;
import handler.StaticFileHandler;
import webserver.request.Request;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ResponseBuilder {
    private final static Map<String, Handler> getPages = new HashMap<>();
    private final static Map<String, Handler> postPages = new HashMap<>();
    private static Map<String, Handler> pages;

    public ResponseBuilder() {
        postPages.put("/user/create", new CreateUserHandler());
        postPages.put("default", new Page404Handler());
        getPages.put("default", new StaticFileHandler());
    }

    public void buildResponse(DataOutputStream dos, Request request) throws IOException {
        switch (request.method){
            case "GET": pages = getPages; break;
            case "POST": pages = postPages; break;
            default: pages = getPages;
        }

        if (pages.containsKey(request.url)) {
            Handler handler = pages.get(request.url);
            handler.handle(dos, request);
        }
        else{
            pages.get("default").handle(dos, request);
        }
    }
}
