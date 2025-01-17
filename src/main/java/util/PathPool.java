package util;

import http.constant.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;
import handler.UserHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.exception.NotAllowedMethodException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class PathPool {
    private static final Logger logger = LoggerFactory.getLogger(PathPool.class);
    private final ConcurrentHashMap<String, ConcurrentHashMap<HttpMethod, Method>> methodMap = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Object> classMap = new ConcurrentHashMap<>();
    private static final PathPool instance;

    static {
        try {
            instance = new PathPool();
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    private PathPool() throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {

        Constructor<UserHandler> c = UserHandler.class.getDeclaredConstructor();
        UserHandler userHandler = c.newInstance();

        Method createUser = userHandler.getClass().getDeclaredMethod("createUser", HttpRequest.class, HttpResponse.class);
        Method loginUser = userHandler.getClass().getDeclaredMethod("loginUser", HttpRequest.class, HttpResponse.class);
        Method logoutUser = userHandler.getClass().getDeclaredMethod("logoutUser", HttpRequest.class, HttpResponse.class);

        ConcurrentHashMap<HttpMethod, Method> createUserMethods = new ConcurrentHashMap<>();
        ConcurrentHashMap<HttpMethod, Method> loginUserMethods = new ConcurrentHashMap<>();
        ConcurrentHashMap<HttpMethod, Method> logoutUserMethods = new ConcurrentHashMap<>();

        createUserMethods.put(HttpMethod.POST, createUser);
        loginUserMethods.put(HttpMethod.POST, loginUser);
        logoutUserMethods.put(HttpMethod.POST, logoutUser);

        methodMap.put("/user/create", createUserMethods);
        methodMap.put("/user/login", loginUserMethods);
        methodMap.put("/user/logout", logoutUserMethods);
        classMap.put("/user/create", userHandler);
        classMap.put("/user/login", userHandler);
        classMap.put("/user/logout", userHandler);
    }

    public boolean isAvailable(HttpMethod method, String path) {
        if (!classMap.containsKey(path)) {
            return false;
        }
        if (!methodMap.get(path).containsKey(method)) {
            throw new NotAllowedMethodException();
        }
        return true;
    }

    public static PathPool getInstance() {
        return instance;
    }

    public Method getMethod(HttpMethod method, String path) {
        return methodMap.get(path).get(method);
    }

    public Object getClass(String path) {
        return classMap.get(path);
    }

}
