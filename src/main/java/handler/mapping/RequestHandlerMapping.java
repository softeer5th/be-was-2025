package handler.mapping;

import handler.request.RequestHandler;
import handler.request.UserLoginRequestHandler;
import handler.request.UserLogoutRequestHandler;
import handler.request.UserSignUpRequestHandler;
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
        handlerMap.put(new HandlerKey("/logout", HttpMethod.GET), new UserLogoutRequestHandler());
    }

    public Optional<RequestHandler> getHandler(HttpRequest httpRequest){
        return Optional.ofNullable(handlerMap.get(new HandlerKey(httpRequest.getPath(), httpRequest.getMethod())));
    }
}
