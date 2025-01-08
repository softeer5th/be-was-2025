package handler;

import java.util.Optional;
import java.util.regex.Pattern;

public enum RequestRoute {
    USER(Pattern.compile("^/user/.+$"), new UserRequestHandler()),
    STATIC_FILE(Pattern.compile("^.*\\.(html|css|js|svg)$"), new StaticFileHandler());

    private final Pattern requestPattern;
    private final Handler handler;

    RequestRoute(Pattern requestPattern, Handler handler) {
        this.requestPattern = requestPattern;
        this.handler = handler;
    }

    public Pattern getRequestPattern() {
        return requestPattern;
    }

    public Handler getHandler() {
        return handler;
    }

    public static Optional<Handler> getHandler(String path) {
        for (RequestRoute route : RequestRoute.values()) {
            if (route.getRequestPattern().matcher(path).matches()) {
                return Optional.of(route.getHandler());
            }
        }
        return Optional.empty();
    }
}
