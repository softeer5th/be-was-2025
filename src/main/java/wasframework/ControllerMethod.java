package wasframework;

import java.lang.reflect.Method;

public class ControllerMethod {
    private final Object controller;
    private final Method method;

    public ControllerMethod(Object controller, Method method) {
        this.controller = controller;
        this.method = method;
    }
}
