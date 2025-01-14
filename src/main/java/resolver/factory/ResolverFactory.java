package resolver.factory;

import resolver.ResourceResolver;
import resolver.SequentialResolver;
import resolver.StaticResourceResolver;

import javax.naming.spi.Resolver;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ResolverFactory {
    public static ResourceResolver createResolver() {
        try {
            ResourceResolver methodResolver = MethodResolverFactory.createResolver(List.of());
            ResourceResolver staticResolver = StaticResourceResolver.getInstance();
            return new SequentialResolver(methodResolver, staticResolver);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
