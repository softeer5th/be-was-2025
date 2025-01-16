package webserver;

import Response.HTTPResponse;
import request.HTTPRequest;

import constant.HTTPCode;
import manager.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


public class DynamicURIHandler implements URIHandler {
    private static final Logger logger = LoggerFactory.getLogger(DynamicURIHandler.class);
    private static final Map<String, Method> uriMethodMap = new HashMap<>();

    public DynamicURIHandler() {
        initMap();
    }

    private void initMap() {
        try{
            uriMethodMap.put("POST:/user/create", UserManager.class.getMethod("signUp", HTTPRequest.class));
            uriMethodMap.put("POST:/user/login", UserManager.class.getMethod("logIn", HTTPRequest.class));
        }
        catch(Exception e){
            logger.error(e.getMessage());
        }
    }

    @Override
    public boolean supports(HTTPRequest httpRequest) {
        Method method = uriMethodMap.get(generateUriMethodKey(httpRequest.getHttpMethod(), httpRequest.getUri()));
        return method != null;
    }

    @Override
    public HTTPResponse handle(HTTPRequest httpRequest){

        Method method = uriMethodMap.get(generateUriMethodKey(httpRequest.getHttpMethod(), httpRequest.getUri()));
        try {
            Object instance = method.getDeclaringClass().getDeclaredConstructor().newInstance();
            Object result = method.invoke(instance, httpRequest);
            logger.debug("Successfully invoked method: {} for URI: {}",
                    method.getName(), httpRequest.getUri());
            return (HTTPResponse) result;
        } catch (Exception e) {
            logger.error("Fail to invoke method for " + httpRequest.getUri());
            return HTTPResponse.createFailResponse(httpRequest.getHttpVersion(),HTTPCode.BAD_REQUEST);
        }
    }

    private String generateUriMethodKey(String httpMethod, String uri) {
        return httpMethod + ":" + uri.split("\\?")[0];
    }

}
