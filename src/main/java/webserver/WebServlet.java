package webserver;

import api.Controller;
import api.UserController;
import http.enums.HttpMethod;
import http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class WebServlet {
    private static final WebServlet INSTANCE = new WebServlet();
    private final Map<String, Controller> controllerMap = new HashMap<>();

    public static WebServlet getInstance(){
        return INSTANCE;
    }

    public WebServlet(){
        controllerMap.put("/create", new UserController());
    }

    public void process(HttpRequest httpRequest){
        // 경로에서 api 제거
        String parsedPath = httpRequest.getPath().substring(4);

        Controller controller = controllerMap.get(parsedPath);

        if(httpRequest.getMethod().equals(HttpMethod.GET)){
            controller.processGet(httpRequest);
        }
    }

}
