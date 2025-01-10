package util;


import handler.CreateUserHandler;
import handler.Handler;
import handler.StaticFileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ResponseBuilder {
    private static final Logger logger = LoggerFactory.getLogger(ResponseBuilder.class);
    private static final Map<String, Handler> responses = new HashMap<>();

    public ResponseBuilder() {
        responses.put("/user/create", new CreateUserHandler());
    }

    public void buildResponse(DataOutputStream dos, RequestParser requestParser) throws IOException {
        if (responses.containsKey(requestParser.url)) {
            Handler handler = responses.get(requestParser.url);
            handler.handle(dos, requestParser);
        }
        else{
            Handler handler = new StaticFileHandler();
            handler.handle(dos, requestParser);
        }
    }
}
