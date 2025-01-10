package webserver;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.file.Files;

import db.Database;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
    private static final String resourcePath = "src/main/resources/static/";

    private Socket connection;

    public RequestHandler(Socket connectionSocket) {
        this.connection = connectionSocket;
    }

    public void run() {
        logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
            DataOutputStream dos = new DataOutputStream(out);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line = br.readLine();
            String[] token = line.split(" ");

            // Request의 HTTP 헤더 출력
            StringBuilder requestHeader = new StringBuilder();
            requestHeader.append("Request Header: \n");
            requestHeader.append(line + "\n");
            while (!"".equals(line)) {
                requestHeader.append((line = br.readLine()) + "\n");
            }
            logger.debug(requestHeader.toString());

            // Request의 uri를 추출
            String[] uri = token[1].split("\\?");
            String path = uri[0];
            String params = (uri.length > 1) ? uri[1] : "";
            logger.debug("path: {}, params: {}", path, params);

            // path 기준으로 탐색
            // default page에 대한 처리
            if (path.equals("/")) {
                byte[] body = "<h2>Hello World</h2>".getBytes();
                ResponseHandler.respond(dos, body, null, 200);
            }
            // 회원가입 요청에 대한 처리
            else if (path.equals("/registration")) {
                File file = new File(resourcePath + "registration/index.html");
                if (file.exists()) {
                    byte[] body = Files.readAllBytes(file.toPath());
                    ResponseHandler.respond(dos, body, ".html", 200);
                } else {
                    logger.error("{}File not found", path);
                    byte[] body = "<h2> HTTP 404 Not Found</h2>".getBytes();

                    // 404 Not Found
                    ResponseHandler.respond(dos, body, null, 404);
                }
            }
            // 회원가입 완료에 대한 처리
            else if (path.startsWith("/user/create")) {
                String[] param = params.split("&");
                String userId = URLDecoder.decode(param[0].substring(param[0].indexOf("=") + 1), "UTF-8");
                String userName = URLDecoder.decode(param[1].substring(param[1].indexOf("=") + 1), "UTF-8");
                String userPassword = URLDecoder.decode(param[2].substring(param[2].indexOf("=") + 1), "UTF-8");
                logger.debug("userId: {}, userName: {}, userPassword: {}", userId, userName, userPassword);

                // 중복된 id를 가진 사용자가 없을 경우
                if (Database.findUserById(userId) == null) {
                    // User 데이터베이스에 사용자 정보 추가
                    User user = new User(userId, userName, userPassword);
                    Database.addUser(user);

                    // 메인 화면으로 리다이렉트
                    ResponseHandler.respond302(dos, "/main/index.html");
                }
                // 중복된 id를 가진 사용자가 있을 경우
                else {
                    logger.error("User already exists");

                    // 409 Conflict
                    ResponseHandler.respond(dos, null, null, 409);
                }
            } else {
                File file = new File(resourcePath + path);
                // file 요청에 대한 처리
                if (file.exists()) {
                    byte[] body = Files.readAllBytes(file.toPath());

                    ResponseHandler.respond(dos, body, path, 200);
                }
                // 유효하지 않은 path에 대한 처리
                else {
                    logger.error("{}path not found", path);
                    byte[] body = "<h2> HTTP 400 Bad Request</h2>".getBytes();

                    // 400 Bad Request
                    ResponseHandler.respond(dos, body, null, 400);
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}