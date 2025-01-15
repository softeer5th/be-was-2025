    package webserver;

    import java.io.*;
    import java.net.Socket;
    import java.net.URLDecoder;
    import java.nio.charset.StandardCharsets;
    import java.nio.file.Files;
    import java.util.Map;

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

        // Todo: 에러 발생 시 클라이언트에게 응답하는 기능 구현 필요
        // Todo: timeout 발생 시 처리하는 기능 구현 필요
        public void run() {
            logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                    connection.getPort());

            HTTPRequestHeader requestHeader = null;
            HTTPRequestBody requestBody = null;

            DataOutputStream dos = null;
            try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
                dos = new DataOutputStream(out);
                // 스트림 데이터를 읽기 위한 버퍼 생성
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] temp = new byte[1024];
                int bytesRead;

                // header 읽기
                StringBuilder headerBuilder = new StringBuilder();
                boolean headerEnd = false;

                while (!headerEnd && (bytesRead = in.read(temp)) != -1) {
                    for (int i = 0; i < bytesRead; i++) {
                        char c = (char) temp[i];
                        headerBuilder.append(c);

                        // body가 있을 경우에만 진입 - header의 끝인지 검사
                        if (headerBuilder.length() >= 4 && headerBuilder.substring(headerBuilder.length() - 4).equals("\r\n\r\n")) {
                            headerEnd = true;
                            // 남은 데이터는 body로 넘김
                            buffer.write(temp, i + 1, bytesRead - i - 1);
                            break;
                        }
                    }
                }
                String headersString = headerBuilder.toString();
                logger.debug(headersString);

                try {
                    requestHeader = new HTTPRequestHeader(headersString);
                } catch (HTTPExceptions e) {
                    logger.error(e.getMessage());
                    byte[] responseBody = HTTPExceptions.getErrorMessage(e.getMessage());
                    try {
                        ResponseHandler.respond(dos, responseBody, null, e.getStatusCode());
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                    return;
                }


                String method = requestHeader.getMethod();
                String[] uri = requestHeader.getUri().split("\\?");
                String version = requestHeader.getVersion();
                Map<String, String> headers = requestHeader.getHeaders();

                // body가 있을 경우 body 읽기
                if (headers.containsKey("content-length")) {
                    int contentLength = Integer.parseInt(headers.get("content-length"));

                    while (buffer.size() < contentLength && (bytesRead = in.read(temp)) != -1) {
                        buffer.write(temp, 0, bytesRead);
                    }

                    byte[] body = buffer.toByteArray();

                    // Content-Length와 body 길이가 다른 경우
                    // 400 Bad Request
                    if (body.length != contentLength) {
                        HTTPExceptions e = new HTTPExceptions.Error400("400 Bad Request: Content-Length header mismatch");
                        logger.error(e.getMessage());
                        byte[] responseBody = HTTPExceptions.getErrorMessage(e.getMessage());
                        try {
                            ResponseHandler.respond(dos, responseBody, null, e.getStatusCode());
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                        return;
                    }

                    logger.debug("Body: {}", new String(body, StandardCharsets.UTF_8));

                    requestBody = new HTTPRequestBody(body);
                }
                // Todo: Content-Type에 따라 적합한 형태로 body 변환하기

                // Request의 uri를 추출
                String path = uri[0];
                String params = (uri.length > 1) ? uri[1] : "";
                logger.debug("path: {}, params: {}", path, params);
                logger.debug("method: {}", method);

                // path 기준으로 탐색
                // default page에 대한 처리
                // Todo: 단순히 path를 if-else문으로 하면 길게 나열되는 문제 발생. 이에 대해 처리하는 방법 구상하기
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
                // Todo: param을 key-value 쌍으로 만들어서 저장할 수 있도록 코드 수정
                // Todo: url 변조 공격을 방지하기 위해 서버 측에서 유효한 회원정보를 입력받았는지 검사하는 코드 추가 (클라이언트 측과 동일한 검사 수행)
                // Todo: 수정된 방식을 적용하면 id라는 key 대신에 "id=123", value에 456이 들어가게 되면 key: "id", value: "123=456" 이렇게 되는 문제 발생. 이 버그 수정 필요
                else if (path.equals("/user/create") && method.equals("POST")) {
                    String[] param = requestBody.getBodyToString().split("&");
                    String userId = URLDecoder.decode(param[0].substring(param[0].indexOf("=") + 1), "UTF-8");
                    String userName = URLDecoder.decode(param[1].substring(param[1].indexOf("=") + 1), "UTF-8");
                    String userPassword = URLDecoder.decode(param[2].substring(param[2].indexOf("=") + 1), "UTF-8");
                    logger.debug("Creating User");
                    logger.debug("Body: {}", requestBody.getBodyToString());

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
                        // 409 Conflict
                        HTTPExceptions e = new HTTPExceptions.Error409("409 Conflict: User already exists");
                        logger.error(e.getMessage());
                        byte[] responseBody = HTTPExceptions.getErrorMessage(e.getMessage());
                        try {
                            ResponseHandler.respond(dos, responseBody, null, e.getStatusCode());
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
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
                        HTTPExceptions e = new HTTPExceptions.Error404("404 Not Found");
                        logger.error(e.getMessage());
                        byte[] responseBody = HTTPExceptions.getErrorMessage(e.getMessage());
                        try {
                            ResponseHandler.respond(dos, responseBody, null, e.getStatusCode());
                        } catch (IOException ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                }
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }