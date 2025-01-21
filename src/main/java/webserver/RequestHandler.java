    package webserver;

    import java.io.*;
    import java.net.Socket;
    import java.net.URLDecoder;
    import java.nio.charset.StandardCharsets;
    import java.nio.file.Files;
    import java.util.*;

    import db.Database;
    import model.User;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;

    public class RequestHandler implements Runnable {
        private static final int MAX_LOGIN_SESSION_TIME = 3600;
        private static final String DEFAULT_HTTP_VERSION = "HTTP/1.1";

        private static final Logger logger = LoggerFactory.getLogger(RequestHandler.class);
        private static final String resourcePath = "src/main/resources/static/";

        private Socket connection;

        public RequestHandler(Socket connectionSocket) {
            this.connection = connectionSocket;
        }

        // Todo: timeout 발생 시 처리하는 기능 구현 필요
        public void run() {
            logger.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                    connection.getPort());

            HTTPRequestHeader requestHeader;
            HTTPRequestBody requestBody = null;

            HTTPResponseHeader responseHeader = new HTTPResponseHeader(DEFAULT_HTTP_VERSION);
            HTTPResponseBody responseBody = null;
            List<Cookie> cookieList = new ArrayList<>();

            try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
                DataOutputStream dos = new DataOutputStream(out);
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

                    String method = requestHeader.getMethod();
                    String[] uri = requestHeader.getUri().split("\\?");
                    String version = requestHeader.getVersion();
                    Map<String, String> headers = requestHeader.getHeaders();

                    responseHeader.setVersion(version);

                    if (headers.containsKey("cookie")) {
                        // 새로운 쿠키 값을 처리해야 할 경우 이 부분에 추가하면 된다.
                        Map<String, String> cookies = Cookie.parseCookies(headers.get("cookie"));
                        if (cookies.containsKey("SESSIONID")) {
                            String sessionId = cookies.get("SESSIONID");

                            Database.updateSessionLastAccessTime(sessionId);
                            cookieList.add(new Cookie("SESSIONID", sessionId, Database.findSessionMaxInactiveInterval(sessionId)));
                        }
                    }
                    // body가 있을 경우 body 읽기
                    if (headers.containsKey("content-length")) {
                        int contentLength = Integer.parseInt(headers.get("content-length"));

                        while (buffer.size() < contentLength && (bytesRead = in.read(temp)) != -1) {
                            buffer.write(temp, 0, bytesRead);
                        }

                        byte[] body = buffer.toByteArray();

                        // Content-Length와 body 길이가 다른 경우
                        if (body.length != contentLength) {
                            throw new HTTPExceptions.Error400("400 Bad Request: Content-Length header mismatch");
                        }

                        logger.debug("Body: {}", new String(body, StandardCharsets.UTF_8));

                        requestBody = new HTTPRequestBody(body);
                    }
                    // Todo: Content-Type에 따라 적합한 형태로 body 변환하기

                    // Request의 uri를 추출
                    String path = uri[0];
                    String queryParams = (uri.length > 1) ? uri[1] : "";
                    logger.debug("path: {}, params: {}", path, queryParams);
                    logger.debug("method: {}", method);

                    // Todo: 단순히 path를 if-else문으로 하면 길게 나열되는 문제 발생. 이에 대해 처리하는 방법 구상하기
                    // Todo: POST 요청에서 파싱하는 과정을 메서드로 처리할 수 있을 듯 함.
                    // path 기준으로 탐색
                    // default page
                    if (path.equals("/")) {
                        responseHeader.setStatusCode(302);
                        responseHeader.addHeader("Location", "/index.html");
                    }
                    // 회원가입 요청에 대한 처리
                    else if (path.equals("/registration")) {
                        path = "/registration/index.html";
                        File file = new File(resourcePath + path);
                        if (file.exists()) {
                            responseBody = new HTTPResponseBody(Files.readAllBytes(file.toPath()));

                            responseHeader.setStatusCode(200);
                            responseHeader.addHeader("Content-Type", ContentTypeMapper.getContentType(path));
                            responseHeader.addHeader("Content-Length", Integer.toString(responseBody.getBodyLength()));
                        } else {
                            logger.error("{}File not found", path);
                            throw new HTTPExceptions.Error404("404 File not found");
                        }
                    }
                    // 로그인 요청에 대한 처리
                    else if (path.equals("/login")) {
                        File file = new File(resourcePath + "login/index.html");
                        if (file.exists()) {
                            responseBody = new HTTPResponseBody(Files.readAllBytes(file.toPath()));

                            responseHeader.setStatusCode(200);
                            responseHeader.addHeader("Content-Type", ContentTypeMapper.getContentType(".html"));
                            responseHeader.addHeader("Content-Length", Integer.toString(responseBody.getBodyLength()));
                        } else {
                            logger.error("{}File not found", path);
                            throw new HTTPExceptions.Error404("404 File not found");
                        }
                    }
                    // 마이페이지 요청에 대한 처리
                    else if (path.equals("/mypage")) {
                        boolean isLoggedIn = false;
                        for (Cookie cookie : cookieList) {
                            if (cookie.getName().equals("SESSIONID")) {
                                isLoggedIn = true;
                                break;
                            }
                        }

                        if (!isLoggedIn) {
                            responseHeader.setStatusCode(302);
                            responseHeader.addHeader("Location", "/login");
                            return;
                        }

                        File file = new File(resourcePath + "mypage/index.html");
                        if (!file.exists()) {
                            logger.error("{}File not found", path);
                            throw new HTTPExceptions.Error404("404 File not found");
                        }

                        responseBody = new HTTPResponseBody(Files.readAllBytes(file.toPath()));

                        responseHeader.setStatusCode(200);
                        responseHeader.addHeader("Content-Type", ContentTypeMapper.getContentType(".html"));
                        responseHeader.addHeader("Content-Length", Integer.toString(responseBody.getBodyLength()));
                    }
                    // 로그인 완료에 대한 처리
                    else if (path.equals("/user/login") && method.equals("POST")) {
                        // 지정된 Content-Type이 아닐 경우
                        if (!headers.get("content-type").equals("application/x-www-form-urlencoded")) {
                            throw new HTTPExceptions.Error415("415 Unsupported Media Type");
                        }

                        String[] params = requestBody.getBodyToString().split("&");
                        Map<String, String> paramMap = new HashMap<>();
                        for (String param : params) {
                            String[] keyValue = param.split("=");
                            // 키값에 등호가 있을 경우
                            if (keyValue.length != 2) {
                                throw new HTTPExceptions.Error400("400 Bad Request: Invalid key");
                            }
                            // 키값 중복
                            if (paramMap.containsKey(keyValue[0])) {
                                throw new HTTPExceptions.Error400("400 Bad Request: Duplicate key");
                            }
                            paramMap.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
                        }
                        // 잘못된 키값 입력
                        if (paramMap.size() != 2) {
                            throw new HTTPExceptions.Error400("400 Bad Request: wrong number of parameters");
                        }

                        String userId = paramMap.get("id");
                        String userPassword = paramMap.get("password");

                        if (userId == null || userId.isEmpty() || userPassword == null || userPassword.isEmpty()) {
                            throw new HTTPExceptions.Error400("400 Bad Request: missing required parameters");
                        }

                        logger.debug("userId: {}, userPassword: {}", userId, userPassword);

                        User user;
                        if ((user = Database.findUserById(userId)) != null) {
                            if (user.getPassword().equals(userPassword)) {
                                Session session = new Session(userId, MAX_LOGIN_SESSION_TIME);
                                cookieList.add(new Cookie("SESSIONID", session.getSessionId(), session.getMaxInactiveInterval()));

                                Database.addSession(session);

                                responseHeader.setStatusCode(302);
                                responseHeader.addHeader("Location", "/index.html");
                            }
                            else {
                                logger.error("User {} password does not match", userId);
                                responseHeader.setStatusCode(302);
                                responseHeader.addHeader("Location", "/user/login_failed.html");
                            }
                        }
                        else {
                            logger.error("User {} not found", userId);
                            responseHeader.setStatusCode(302);
                            responseHeader.addHeader("Location", "/user/login_failed.html");
                        }
                    }
                    // 로그아웃 요청에 대한 처리
                    else if (path.equals("/user/logout") && method.equals("POST")) {
                        for (Cookie cookie : cookieList) {
                            if (cookie.getName().equals("SESSIONID")) {
                                Database.deleteSession(cookie.getValue());
                            }
                            cookie.expireCookie();
                        }

                        responseHeader.setStatusCode(302);
                        responseHeader.addHeader("Location", "/index.html");
                    }
                    // 회원가입 완료에 대한 처리
                    else if (path.equals("/user/create") && method.equals("POST")) {
                        // 지정된 Content-Type이 아닐 경우
                        if (!headers.get("content-type").equals("application/x-www-form-urlencoded")) {
                            throw new HTTPExceptions.Error415("415 Unsupported Media Type");
                        }

                        String[] params = requestBody.getBodyToString().split("&");
                        Map<String, String> paramMap = new HashMap<>();
                        for (String param : params) {
                            String[] keyValue = param.split("=");
                            // 키값에 등호가 있을 경우
                            if (keyValue.length != 2) {
                                throw new HTTPExceptions.Error400("400 Bad Request: Invalid key");
                            }
                            // 키값 중복
                            if (paramMap.containsKey(keyValue[0])) {
                                throw new HTTPExceptions.Error400("400 Bad Request: Duplicate key");
                            }
                            paramMap.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
                        }
                        // 잘못된 키값 입력
                        if (paramMap.size() != 3) {
                            throw new HTTPExceptions.Error400("400 Bad Request: wrong number of parameters");
                        }

                        String userId = paramMap.get("id");
                        String userName = paramMap.get("name");
                        String userPassword = paramMap.get("password");

                        logger.debug("Creating User");

                        logger.debug("userId: {}, userName: {}, userPassword: {}", userId, userName, userPassword);

                        // 중복된 id를 가진 사용자가 있을 경우
                        if (Database.findUserById(userId) != null) {
                            // 409 Conflict
                            throw new HTTPExceptions.Error409("409 Conflict: User already exists");
                        }

                        // User 데이터베이스에 사용자 정보 추가
                        User user = new User(userId, userPassword, userName);
                        Database.addUser(user);

                        responseHeader.setStatusCode(302);
                        responseHeader.addHeader("Location", "/index.html");
                    }
                    else if (path.equals("/api/login-status")) {
                       boolean isLoggedIn = false;
                       String userName = "";
                       for (Cookie cookie : cookieList) {
                           if (cookie.getName().equals("SESSIONID")) {
                               isLoggedIn = true;
                               String sessionId = cookie.getValue();
                               String userId = Database.findSessionById(sessionId).getUserId();
                               userName = Database.findUserById(userId).getName();
                               break;
                           }
                       }

                       String jsonResponse = "{\"isLoggedIn\": " + isLoggedIn + ", \"userName\": \"" + userName +  "\"}";
                       responseBody = new HTTPResponseBody(jsonResponse.getBytes(StandardCharsets.UTF_8));

                       responseHeader.setStatusCode(200);
                       responseHeader.addHeader("Content-Type", ContentTypeMapper.getContentType(".json"));
                    }  else {
                        File file = new File(resourcePath + path);
                        if (file.exists()) {
                            responseBody = new HTTPResponseBody(Files.readAllBytes(file.toPath()));

                            responseHeader.setStatusCode(200);
                            responseHeader.addHeader("Content-Type", ContentTypeMapper.getContentType(path));
                            responseHeader.addHeader("Content-Length", Integer.toString(responseBody.getBodyLength()));
                        }
                        // 유효하지 않은 path에 대한 처리
                        else {
                            throw new HTTPExceptions.Error404("404 Not Found");
                        }
                    }
                } catch (HTTPExceptions e) {
                    logger.error(e.getMessage());
                    responseHeader.setStatusCode(e.getStatusCode());
                    responseBody = new HTTPResponseBody(HTTPExceptions.getErrorMessageToBytes(e.getMessage()));
                } finally {
                    for (Cookie cookie: cookieList) {
                        responseHeader.addHeader("Set-Cookie", cookie.toString());
                    }
                    ResponseHandler.respond(dos, responseHeader, responseBody);
                }

            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        }
    }