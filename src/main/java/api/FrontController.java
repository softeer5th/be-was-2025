package api;

import http.HttpMethod;
import http.HttpRequest;

import java.util.HashMap;
import java.util.Map;

public class FrontController {
    private static final FrontController INSTANCE = new FrontController();
    private final Map<String, Controller> controllerMap = new HashMap<>();

    public static FrontController getInstance(){
        return INSTANCE;
    }

    public FrontController(){
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
