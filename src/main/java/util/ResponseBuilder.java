package util;


import handler.CreateUserHandler;
import handler.Handler;
import handler.StaticFileHandler;
import webserver.request.Request;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ResponseBuilder {
    private final Map<String, Handler> responses = new HashMap<>();

    public ResponseBuilder() {
        responses.put("/user/create", new CreateUserHandler());
        responses.put("default", new StaticFileHandler());
    }

    public void buildResponse(DataOutputStream dos, Request request) throws IOException {
        if (responses.containsKey(request.url)) {
            Handler handler = responses.get(request.url);
            handler.handle(dos, request);
        }
        else{
            responses.get("default").handle(dos, request);
        }
    }
}
