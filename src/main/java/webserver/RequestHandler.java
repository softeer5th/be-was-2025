package webserver;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import http.*;
import http.enums.HttpStatus;
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
            BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            DataOutputStream dos = new DataOutputStream(out);

            HttpRequest httpRequest = httpRequestResolver.parseHttpRequest(br);

            File resultFile = FileUtil.findFileByPath(httpRequest.getPath());

            if(resultFile != null){
                byte[] data = FileUtil.readFileToByteArray(resultFile);
                httpResponseResolver.sendResponse(dos, HttpStatus.OK, resultFile.getPath(), data);
            }
            else if(httpRequest.getPath().startsWith("/api")){
                webServlet.process(httpRequest);
            }
            else{
                byte[] data = "Request Not Found!".getBytes();
                httpResponseResolver.sendResponse(dos, HttpStatus.NOT_FOUND, httpRequest.getPath(), data);
            }
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

}
