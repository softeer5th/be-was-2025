package util;

import http.HttpRequest;
import http.HttpResponse;
import http.UserHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;

public class PathPool {
    private static final Logger logger = LoggerFactory.getLogger(PathPool.class);
    private final ConcurrentHashMap<String, ConcurrentHashMap<String, Method>> methodMap = new ConcurrentHashMap<>();
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

        ConcurrentHashMap<String, Method> methods = new ConcurrentHashMap<>();
        Constructor<UserHandler> c = UserHandler.class.getDeclaredConstructor();
        UserHandler rp = c.newInstance();

        Method method = rp.getClass().getDeclaredMethod("createUser", HttpRequest.class, HttpResponse.class);

        methods.put("post", method);
        methodMap.put("/user/create", methods);
        classMap.put("/user/create", rp);

    }

    public static PathPool getInstance() {
        return instance;
    }

    public Method getMethod(String method, String path) {
        return methodMap.get(path).get(method);
    }

    public Object getClass(String path) {
        return classMap.get(path);
    }

}
