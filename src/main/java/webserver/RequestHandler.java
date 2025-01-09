package webserver;

import java.io.*;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.FileUtil;
import common.HttpStatus;
import util.HttpResponse;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String BASE_DIRECTORY = "src/main/resources/static";

    private Socket connection;
    private HttpResponse httpResponse = new HttpResponse();

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);


            String requestLine = reader.readLine();
            if (requestLine == null) return;
            logger.debug("Request : {}", requestLine);

            String[] tokens = requestLine.replaceAll("\\s+", " ").trim().split(" ");
            String filepath = FileUtil.getFilePath(BASE_DIRECTORY + tokens[1]);
            byte[] body = FileUtil.readHtmlFileAsBytes(filepath);
            if (body != null) {
                httpResponse.responseHeader(HttpStatus.OK, dos, body.length, filepath);
                httpResponse.responseBody(dos, body);
            } else {
                httpResponse.responseHeader(HttpStatus.NOT_FOUND, dos, 0, null);
                httpResponse.responseBody(dos, null);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
