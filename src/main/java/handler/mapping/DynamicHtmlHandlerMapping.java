package handler.mapping;

import handler.dynamic_handler.DynamicHtmlHandler;
import handler.dynamic_handler.HomeDynamicHtmlHandler;
import handler.dynamic_handler.MyPageDynamicHtmlHandler;
import handler.dynamic_handler.WriteDynamicHtmlHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DynamicHtmlHandlerMapping {
    private static final DynamicHtmlHandlerMapping INSTANCE = new DynamicHtmlHandlerMapping();
    private Map<String, DynamicHtmlHandler> handlerMap = new HashMap<>();

    private DynamicHtmlHandlerMapping(){
        handlerMap.put("/", new HomeDynamicHtmlHandler());
        handlerMap.put("/mypage", new MyPageDynamicHtmlHandler());
        handlerMap.put("/write", new WriteDynamicHtmlHandler());
    }
    public static DynamicHtmlHandlerMapping getInstance(){
        return INSTANCE;
    }

    public Optional<DynamicHtmlHandler> getHandler(String path){
        return Optional.ofNullable(handlerMap.get(path));
    }
}
