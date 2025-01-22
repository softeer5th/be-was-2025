package webserver.resolver.factory;

import db.Database;
import db.connection.ConnectionProvider;
import db.connection.RealConnectionProvider;
import entrypoint.UserEntryPoint;
import webserver.resolver.ResourceResolver;
import webserver.resolver.SequentialResolver;
import webserver.resolver.StaticResourceResolver;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class ResolverFactory {
    private static final ConnectionProvider connectionProvider = new RealConnectionProvider();
    private static final Database database = new Database(connectionProvider);

    public static ResourceResolver createResolver() {
        try {
            ResourceResolver methodResolver = MethodResolverFactory.createResolver(
                    List.of(
                            new UserEntryPoint(database)
                    )
            );
            ResourceResolver staticResolver = StaticResourceResolver.getInstance();
            return new SequentialResolver(methodResolver, staticResolver);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
