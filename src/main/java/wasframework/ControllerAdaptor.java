package wasframework;

import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

public class ControllerAdaptor {

    /**
     * 적절한 파라미터를 매핑하여 컨트롤러의 핸들러를 invoke 해주는 메소드
     * @param controllerMethod
     * @param request
     * @param response
     * @throws InvocationTargetException 컨트롤러 내에서 예외가 발생했을 경우
     * @throws RuntimeException 서버의 핸들러 중 private 메소드에 매핑된 핸들러가 있는 경우
     */
    public void invoke(ControllerMethod controllerMethod, HttpRequest request, HttpResponse response) throws InvocationTargetException {

        Method method = controllerMethod.method();
        Parameter[] parameters = method.getParameters();
        Object[] args = new Object[parameters.length];

        Map<String, String> pathVariables = extractPathVariables(method, request);

        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];

            if (parameter.isAnnotationPresent(PathVariable.class)) {
                String paramName = parameter.getAnnotation(PathVariable.class).value();
                String rawValue = pathVariables.get(paramName);
                args[i] = convertType(rawValue, parameter.getType());
            } else if (parameter.getType().equals(HttpRequest.class)) {
                args[i] = request;
            } else if (parameter.getType().equals(HttpResponse.class)) {
                args[i] = response;
            }
        }
        try{
            method.invoke(controllerMethod.controller(), args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("서버의 매핑이 잘못됨 - public 이 아닌 메소드에 @Mapping 사용 중", e);
        }
    }

    private Map<String, String> extractPathVariables(Method method, HttpRequest request) {
        Map<String, String> result = new HashMap<>();

        String requestUri = request.getUri();
        String[] requestPathParts = requestUri.split("/");

        Mapping mappingAnnotation = method.getDeclaredAnnotation(Mapping.class);
        String controllerPath = mappingAnnotation.path();
        String[] controllerPathParts = controllerPath.split("/");

        for (int i = 0; i < controllerPathParts.length; i++) {
            String part = controllerPathParts[i];
            if (part.startsWith("{") && part.endsWith("}")) {
                String variableName = part.substring(1, part.length() - 1);
                if (i < requestPathParts.length) {
                    result.put(variableName, requestPathParts[i]);
                }
            }
        }
        return result;
    }

    private Object convertType(String value, Class<?> targetType) {
        if (value == null) {
            return null;
        }
        if (targetType.equals(String.class)) {
            return value;
        } else if (targetType.equals(Integer.class) || targetType.equals(int.class)) {
            return Integer.parseInt(value);
        } else if (targetType.equals(Long.class) || targetType.equals(long.class)) {
            return Long.parseLong(value);
        }
        return null;
    }
}
