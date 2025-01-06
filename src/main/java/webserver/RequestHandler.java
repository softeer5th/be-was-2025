package webserver;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import ch.qos.logback.core.net.SyslogOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private final String STATIC_FILE_DIRECTORY_PATH = "src/main/resources/static";

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

        public void run() {
            logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                    connection.getPort());

            try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
                DataOutputStream dos = new DataOutputStream(out);

                String [] httpRequestHeader = readInputToArray(in);
                String resourceName = httpRequestHeader[0].split(" ")[1];

                File file = new File(STATIC_FILE_DIRECTORY_PATH, resourceName);

                byte[] body = fileToByteArray(file);

                response200Header(dos, body.length, resourceName);
                responseBody(dos, body);

            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }

    public static String[] readInputToArray(InputStream in) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        List<String> lines = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            lines.add(line);
        }

        return lines.toArray(new String[0]);
    }

    public static byte[] fileToByteArray(File file) {
        byte[] fileBytes = new byte[(int) file.length()]; // 파일 크기만큼 배열 생성

        try (FileInputStream fis = new FileInputStream(file)) {
            int bytesRead = fis.read(fileBytes); // 파일 내용 읽기
            if (bytesRead != fileBytes.length) {
                throw new IOException("Could not completely read the file");
            }
            return fileBytes;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void response200Header(DataOutputStream dos, int lengthOfBodyContent, String resourceName) {
        try {
            String contentType = getContentType(resourceName);
            dos.writeBytes("HTTP/1.1 200 OK \r\n");
            dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
            dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
            dos.writeBytes("\r\n");
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    private String getContentType(String resourceName) {
        String extension = resourceName.substring(resourceName.lastIndexOf(".") + 1);

        switch (extension) {
            case "html":
                return "text/html";
            case "css":
                return "text/css";
            case "js":
                return "text/javascript";
            case "ico":
                return "image/x-icon";
            case "png":
                return "image/png";
            case "jpg":
                return "image/jpeg";
            case "svg":
                return "image/svg+xml";
            default:
                return "text/plain";
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
}
