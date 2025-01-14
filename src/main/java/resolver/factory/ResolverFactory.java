package resolver.factory;

import entrypoint.UserEntryPoint;
import resolver.ResourceResolver;
import resolver.SequentialResolver;
import resolver.StaticResourceResolver;

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
