package handler.mapper;

import handler.RequestHandler;
import handler.UserRequestHandler;

import java.util.HashMap;
import java.util.Map;

public class RequestHandlerMapper {
    private final Map<String, RequestHandler> requestHandlerMap = new HashMap<>();

    public RequestHandlerMapper(){
        requestHandlerMap.put("/create", new UserRequestHandler());
    }

    public RequestHandler mapRequestHandler(String path){
        return requestHandlerMap.get(path);
    }
}
