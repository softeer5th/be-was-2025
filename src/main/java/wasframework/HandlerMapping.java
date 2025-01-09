package wasframework;

import webserver.httpserver.HttpMethod;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class HandlerMapping {
    private static final String DASH = "/";
    private final List<Object> controllers;

    public HandlerMapping(List<Object> controllers) {
        validateControllers(controllers);
        this.controllers = controllers;
    }

    public Optional<ControllerMethod> getControllerMethod(String path, HttpMethod httpMethod) {
        for (Object controller : controllers) {
            Method[] methods = controller.getClass().getMethods();
            for (Method method : methods) {
                if (isSupportedHandler(path, httpMethod, method)) {
                    return Optional.of(new ControllerMethod(controller, method));
                }
            }
        }
        return Optional.empty();
    }

    private boolean isSupportedHandler(String path, HttpMethod httpMethod, Method method) {
        Mapping mappingInfo = method.getDeclaredAnnotation(Mapping.class);
        if (isSameHttpMethod(httpMethod, mappingInfo)) {
            String[] requestPathParts = path.split(DASH);
            String[] controllerPathParts = mappingInfo.path().split(DASH);
            return isSupportedUri(requestPathParts, controllerPathParts);
        }
        return false;
    }

    private boolean isSameHttpMethod(HttpMethod httpMethod, Mapping mappingInfo) {
        return mappingInfo != null && httpMethod == mappingInfo.method();
    }

    private boolean isSupportedUri(String[] requestPathParts, String[] controllerPathParts) {
        if (requestPathParts.length != controllerPathParts.length) {
            return false;
        }
        for (int i = 0; i < requestPathParts.length; i++) {
            if (!requestPathParts[i].equals(controllerPathParts[i])) {
                if (!requestPathParts[i].startsWith("{") || !requestPathParts[i].endsWith("}")) {
                    return false;
                }
            }
        }
        return true;
    }

    private void validateControllers(List<Object> controllers) {
        Map<UrlMapping, Method> controllerMethods = new HashMap<>();
        for (Object controller : controllers) {
            Method[] declaredMethods = controller.getClass().getDeclaredMethods();
            for (Method method : declaredMethods) {
                validateControllerMethod(method, controllerMethods);
            }
        }
    }

    private void validateControllerMethod(Method method, Map<UrlMapping, Method> controllerMethods) {
        Mapping mappingInfo = method.getDeclaredAnnotation(Mapping.class);
        if (mappingInfo != null) {
            UrlMapping urlMapping = new UrlMapping(mappingInfo.path(), mappingInfo.method());
            if(controllerMethods.containsKey(urlMapping)) {
                throw new IllegalArgumentException("Controller method already exists: " + urlMapping + ", " +
                        "First Method: " + controllerMethods.get(urlMapping) + ", Second Method: " + method);
            }
            controllerMethods.put(urlMapping, method);
        }
    }

    private record UrlMapping(String path, HttpMethod httpMethod) {
    }
}
