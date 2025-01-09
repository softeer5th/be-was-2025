package webserver;

import java.io.*;
import java.net.Socket;

import http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtil;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final HttpRequestResolver httpRequestResolver = HttpRequestResolver.getInstance();
    private final HttpResponseResolver httpResponseResolver = HttpResponseResolver.getInstance();
    private final WebServlet webServlet = WebServlet.getInstance();

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream();OutputStream out = connection.getOutputStream()) {
            BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = httpRequestResolver.parseHttpRequest(br);

            File resultFile = FileUtil.findFileByPath(httpRequest.getPath());

            if(resultFile != null){
                byte[] data = FileUtil.readFileToByteArray(resultFile);
                httpResponseResolver.send200Response(dos, resultFile.getPath(), data);
            }
            else if(httpRequest.getPath().startsWith("/api")){
                webServlet.process(httpRequest);
            }
            else{
                httpResponseResolver.send404Response(dos);
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
