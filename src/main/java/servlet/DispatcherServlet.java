package servlet;

import exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wasframework.ControllerAdaptor;
import wasframework.ControllerMapping;
import wasframework.ControllerMethod;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

/**
 * 가장 먼저 요청을 가져와 동적 리소스를 찾고 매핑하는 서블릿
 */
public class DispatcherServlet implements Servlet {
    private static final Logger log = LoggerFactory.getLogger(DispatcherServlet.class);
    private final ControllerMapping controllerMapping;
    private final ControllerAdaptor controllerAdaptor = new ControllerAdaptor();

    public DispatcherServlet(List<Object> controllers) {
        this.controllerMapping = new ControllerMapping(controllers);
    }

    /**
     * 적절한 컨트롤러를 찾아 처리하고, true 를 리턴하는 디스패처 서블릿의 핸들러
     * @param request
     * @param response
     * @return 적절한 핸들러를 찾았을 시 true, 적절한 핸들러를 찾지 못했거나, 핸들러가 처리 중 예외가 발생했을 경우 false 반환
     */
    @Override
    public boolean handle(HttpRequest request, HttpResponse response) {
        ControllerMethod controllerMethod = controllerMapping
                .getControllerMethod(request.getUri(), request.getMethod());
        if (controllerMethod == null) {
            return false;
        }
        try {
            controllerAdaptor.invoke(controllerMethod,request, response);
        } catch (InvocationTargetException e) {
            if (e.getTargetException() instanceof BadRequestException){
                log.error("bad request: ", e.getTargetException());
                throw new BadRequestException("Bad Request 요청 발생");
            }
            log.error(e.getTargetException().getMessage(), e.getTargetException());
            return false;
        }
        return true;
    }
}
