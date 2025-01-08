package webserver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.RequestInfo;
import util.RequestParser;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private Socket connection;

    private final Map<Pattern, Handler> routeMap;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        routeMap = new HashMap<>();
        routeMap.put(Pattern.compile("^/user/.+$"), new UserRequestHandler());
        routeMap.put(Pattern.compile("^.*\\.(html|css|js|svg)$"), new StaticFileHandler());
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            RequestInfo requestInfo = RequestParser.parse(in);
            String path = requestInfo.getPath();

            DataOutputStream dos = new DataOutputStream(out);
            Handler handler = getHandler(path)
                    .orElseThrow(() -> new UnsupportedOperationException("No handler found for path" + path));

            handler.handle(requestInfo, dos);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private Optional<Handler> getHandler(String path) {
        for (Pattern pattern : routeMap.keySet()) {
            Matcher matcher = pattern.matcher(path);
            if (matcher.matches())
                return Optional.of(routeMap.get(pattern));
        }
        return Optional.empty();
    }
}
