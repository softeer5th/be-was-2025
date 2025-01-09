package handler.mapper;

import handler.RequestHandler;
import handler.UserRequestHandler;

import java.util.HashMap;
import java.util.Map;

public class RequestHandlerMapper {
    private static final RequestHandlerMapper INSTANCE = new RequestHandlerMapper();
    private final Map<String, RequestHandler> requestHandlerMap = new HashMap<>();

    public static RequestHandlerMapper getInstance(){
        return INSTANCE;
    }
    public RequestHandlerMapper(){
        requestHandlerMap.put("/create", new UserRequestHandler());
    }

    public RequestHandler mapRequestHandler(String path){
        return requestHandlerMap.get(path);
    }
}
