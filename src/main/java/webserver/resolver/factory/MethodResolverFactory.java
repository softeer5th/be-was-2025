package webserver.resolver.factory;

import webserver.resolver.RequestMethodMapper;
import webserver.resolver.RequestMethodWrapper;
import webserver.resolver.ResourceResolver;
import webserver.resolver.records.ParameterMetaInfo;
import webserver.annotation.RequestMapping;
import webserver.annotation.RequestParam;
import webserver.functional.TypeParser;
import webserver.functional.TypeParserFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MethodResolverFactory {
    public static ResourceResolver createResolver(List<Class<?>> handlerGroups) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Map<String, ResourceResolver> requestMap = new HashMap<>();

        for (Class<?> handlerGroupClass : handlerGroups) {
            Object handlerGroup = handlerGroupClass.getDeclaredConstructor().newInstance();
            Method [] methods = handlerGroup.getClass().getDeclaredMethods();

            for (Method method : methods) {
                RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                if (annotation == null) {
                    continue;
                }
                Parameter[] parameters = method.getParameters();
                ParameterMetaInfo[] parameterMetaInfos = new ParameterMetaInfo[parameters.length];
                for (int i = 0 ; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    handleRequestParam(parameter, parameterMetaInfos, i);
                }
                requestMap.put(annotation.path(), new RequestMethodWrapper(handlerGroup, method, parameterMetaInfos));
            }
        }
        return new RequestMethodMapper(requestMap);
    }

    private static void handleRequestParam(Parameter parameter, ParameterMetaInfo [] infos, int index) {
        RequestParam param = parameter.getAnnotation(RequestParam.class);
        if (param == null) {
            return;
        }
        TypeParser typeParser = TypeParserFactory.getTypeParser(parameter.getType());
        ParameterMetaInfo metaInfo = new ParameterMetaInfo(param.key(), param.required(), typeParser);
        infos[index] = metaInfo;
    }
}
