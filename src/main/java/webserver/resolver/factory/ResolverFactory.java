package webserver.resolver.factory;

import entrypoint.UserEntryPoint;
import webserver.resolver.ResourceResolver;
import webserver.resolver.SequentialResolver;
import webserver.resolver.StaticResourceResolver;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ResolverFactory {
    public static ResourceResolver createResolver() {
        try {
            ResourceResolver methodResolver = MethodResolverFactory.createResolver(List.of(UserEntryPoint.class));
            ResourceResolver staticResolver = StaticResourceResolver.getInstance();
            return new SequentialResolver(methodResolver, staticResolver);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
