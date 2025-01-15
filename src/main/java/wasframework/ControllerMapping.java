package wasframework;

import webserver.httpserver.HttpMethod;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerMapping {
    private static final String DASH = "/";
    public static final String WILD_CARD = "\\?";
    private final List<Object> controllers;

    public ControllerMapping(List<Object> controllers) {
        validateControllers(controllers);
        this.controllers = controllers;
    }

    public ControllerMethod getControllerMethod(String path, HttpMethod httpMethod) {
        for (Object controller : controllers) {
            Method[] methods = controller.getClass().getMethods();
            for (Method method : methods) {
                if (isSupportedController(path, httpMethod, method)) {
                    return new ControllerMethod(controller, method);
                }
            }
        }
        return null;
    }

    private boolean isSupportedController(String path, HttpMethod httpMethod, Method method) {
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
                if (!controllerPathParts[i].startsWith("{") || !controllerPathParts[i].endsWith("}")) {
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
            String pathWithWildcard = replacePathWithWildcard(mappingInfo.path());
            UrlMapping urlMapping = new UrlMapping(pathWithWildcard, mappingInfo.method());
            if (controllerMethods.containsKey(urlMapping)) {
                throw new IllegalArgumentException("Controller method already exists: " + urlMapping + ", " +
                        "First Method: " + controllerMethods.get(urlMapping) + ", Second Method: " + method);
            }
            controllerMethods.put(urlMapping, method);
        }
    }


    private String replacePathWithWildcard(String path) {
        String[] pathParts = path.split(DASH);
        for (int i = 0; i < pathParts.length; i++) {
            if (pathParts[i].startsWith("{") && pathParts[i].endsWith("}")) {
                pathParts[i] = WILD_CARD;
            }
        }
        return String.join(DASH, pathParts);
    }

    private record UrlMapping(String path, HttpMethod httpMethod) {
    }
}
