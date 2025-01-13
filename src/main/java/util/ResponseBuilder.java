package util;


import handler.CreateUserHandler;
import handler.Handler;
import handler.StaticFileHandler;

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

    public void buildResponse(DataOutputStream dos, RequestParser requestParser) throws IOException {
        if (responses.containsKey(requestParser.url)) {
            Handler handler = responses.get(requestParser.url);
            handler.handle(dos, requestParser);
        }
        else{
            responses.get("default").handle(dos, requestParser);
        }
    }
}
