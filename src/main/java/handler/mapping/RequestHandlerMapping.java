package handler.mapping;

import handler.RequestHandler;
import handler.UserLoginRequestHandler;
import handler.UserSignUpRequestHandler;
import http.request.HttpRequest;
import http.enums.HttpMethod;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class RequestHandlerMapping {
    private Map<HandlerKey, RequestHandler> handlerMap = new HashMap<>();

    public void init(){
        handlerMap.put(new HandlerKey("/user/create", HttpMethod.POST), new UserSignUpRequestHandler());
        handlerMap.put(new HandlerKey("/login", HttpMethod.POST), new UserLoginRequestHandler());
    }

    public Optional<RequestHandler> getHandler(HttpRequest httpRequest){
        return Optional.ofNullable(handlerMap.get(new HandlerKey(httpRequest.getPath(), httpRequest.getMethod())));
    }
}
