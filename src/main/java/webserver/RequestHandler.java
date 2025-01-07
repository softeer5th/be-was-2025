package webserver;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.MimeTypeMapper;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    private final MimeTypeMapper mimeTypeMapper;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
        this.mimeTypeMapper = new MimeTypeMapper();
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);

            List<String> headers = logAndReturnHeaders(in);

            String[] requestLine = resolveRequestLine(headers.get(0));

            String target = requestLine[1];
            byte[] body = createBody(target);

            String extension = target.split("\\.")[1];

            String mimeType = mimeTypeMapper.getMimeType(extension);

            response200Header(dos, body.length, mimeType);
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String mimetype) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes(String.format("Content-Type: %s;charset=utf-8\r\n", mimetype));
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
            dos.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private List<String> logAndReturnHeaders(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));

        List<String> headers = new ArrayList<>();

        String line = reader.readLine();

        while(!line.isEmpty()) {
            logger.debug(line);
            headers.add(line);
            line = reader.readLine();
        }

        return headers;
    }

    private byte[] createBody(String target) throws IOException {
        InputStream is = new FileInputStream("./src/main/resources/static" + target);
        byte[] body = is.readAllBytes();
        is.close();

        return body;
    }

    private String[] resolveRequestLine(String requestLine) {
        String[] tokens =  requestLine.split(" ");
        if (tokens[1] != null && tokens[1].contentEquals("/")) {
            tokens[1] = "/index.html";
        }
        return tokens;
    }
}
