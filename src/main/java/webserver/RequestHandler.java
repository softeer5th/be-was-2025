package webserver;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            // TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
            RequestParser requestParser = new RequestParser(in);
            requestParser.getLogs();

            DataOutputStream dos = new DataOutputStream(out);
            byte[] body;
            if(requestParser.url.equals("/")) {
                body = "<h1>Hello World</h1>".getBytes();
            }
            else {
                String path = "./src/main/resources/static" + requestParser.url;
                body = readFileToBytes(path);
            }
            response200Header(dos, body.length);
            responseBody(dos, body);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
        try {
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
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

    private byte[] readFileToBytes(String path){
        File file = new File(path);
        byte[] bytes = new byte[(int) file.length()];

        try(FileInputStream fis = new FileInputStream(file)) {
            fis.read(bytes);
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }
        return bytes;
    }
}

class RequestParser{
    private final List<String> requests = new ArrayList<>();
    public String url = "/";
    private static final Logger logger = LoggerFactory.getLogger(RequestParser.class);

    public RequestParser(InputStream in){
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        try {
            String line = br.readLine();
            while (!line.isEmpty()) {
                requests.add(line);
                line = br.readLine();
            }
            setUrl();
        }
        catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private void setUrl(){
        this.url = requests.get(0).split(" ")[1];
    }

    public void getLogs(){
        for(String request : requests){
            logger.debug(request);
        }
    }
}