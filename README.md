# 학습한 내용 정리

---

## 1. HTTP 구조

### 1.1 HTTP 요청/응답 개념

HTTP 요청은 다음과 같은 구조를 가집니다:

#### Request 구조

1. **시작줄(Request Line)**
```bash
GET /index.html HTTP/1.1
```
- 메서드(GET, POST, PUT 등)
- 경로(/index.html)
- HTTP 버전(HTTP/1.1)

2. **헤더(Headers)**
```
Host: localhost:8080
User-Agent: Mozilla/5.0
Accept: text/html
```
- 요청/응답의 메타데이터 포함
- Content-Type, Content-Length 등

3. **본문(Body)**
- POST, PUT 등에서 데이터 전송 시 사용
- 폼 데이터, JSON, XML 등 포함 가능

#### Response 구조
```
HTTP/1.1 200 OK
Content-Type: text/html
Content-Length: 123

<html> ... </html>
```

### 1.2 요청 파싱 구현
```java
public RequestData parse(InputStream in) throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

    // 1) 요청 라인 파싱
    String requestLine = br.readLine(); 

    // 2) 헤더 파싱
    StringBuilder headers = new StringBuilder();
    String line;
    while ((line = br.readLine()) != null && !line.isEmpty()) {
        headers.append(line).append("\n");
    }

    // 3) 본문 파싱
    StringBuilder body = new StringBuilder();
    while (br.ready()) {
        body.append((char) br.read());
    }

    return new RequestData(requestLine, headers.toString(), body.toString());
}
```

### 1.3 MIME 타입(Content-Type)
- 브라우저의 리소스 해석을 위한 필수 요소
- 주요 타입:
    - text/html (HTML 문서)
    - text/css (CSS 파일)
    - application/javascript (JS)
    - image/png (PNG 이미지)

## 2. 동시성(Concurrent) 처리

### 2.1 전통적인 스레드 방식
```java
while ((connection = serverSocket.accept()) != null) {
    Thread thread = new Thread(new RequestHandler(connection));
    thread.start();
}
```
- 매 요청마다 새로운 스레드 생성
- 대량 요청 시 성능 저하 위험

### 2.2 ExecutorService 활용
```java
ExecutorService executorService = Executors.newFixedThreadPool(10);

while (true) {
    Socket connection = serverSocket.accept();
    executorService.execute(new RequestHandler(connection));
}
```
- 스레드 풀 사용으로 리소스 효율적 관리
- 안정적인 대규모 트래픽 처리

## 3. 자바 I/O 스트림

### 3.1 DataOutputStream 활용
```java
private void writeHeader(String status, String contentType, int contentLength) throws IOException {
    dos.writeBytes(status + "\r\n");
    dos.writeBytes("Content-Type: " + contentType + ";charset=utf-8\r\n");
    dos.writeBytes("Content-Length: " + contentLength + "\r\n");
    dos.writeBytes("\r\n");
}

private void writeBody(byte[] body) throws IOException {
    dos.write(body);
    dos.flush();
}
```

## 4. 트러블슈팅 사례

### 4.1 CSS 적용 문제
- **문제**: CSS 파일 적용 실패
- **원인**: 잘못된 Content-Type 설정
- **해결**:
```java
private String guessContentType(String path) {
    if (path.endsWith(".css")) return "text/css";
    if (path.endsWith(".js")) return "application/javascript";
    if (path.endsWith(".png")) return "image/png";
    return "text/html";
}
```

### 4.2 스레드 최적화
- **문제**: 과도한 스레드 생성으로 성능 저하
- **해결**: ExecutorService 도입으로 스레드 풀 관리
- **결과**: 성능과 안정성 향상

### 4.3 로깅 전략
- **이슈**: HTTP 요청 라인 로깅 필요성
- **결정**: 디버깅용 로그 유지, 운영 환경에서 레벨 조정

## 5. 주요 컴포넌트 구조

### 5.1 WebServer
```java
public class WebServer {
    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;

    public static void main(String[] args) throws Exception {
        int port = (args.length == 0) ? DEFAULT_PORT : Integer.parseInt(args[0]);
        ExecutorService executorService = Executors.newFixedThreadPool(10);

        try (ServerSocket listenSocket = new ServerSocket(port)) {
            logger.info("Web Application Server started {} port.", port);

            while (true) {
                Socket connection = listenSocket.accept();
                executorService.execute(new RequestHandler(connection));
            }
        }
    }
}
```

### 5.2 RequestHandler
```java
public class RequestHandler implements Runnable {
    private final Socket connection;
    private final HttpRequestParser requestParser = new HttpRequestParser();
    private final StaticResourceLoader resourceLoader;

    @Override
    public void run() {
        try (InputStream in = connection.getInputStream();
             OutputStream out = connection.getOutputStream()) {
            
            RequestData requestData = requestParser.parse(in);
            byte[] resource = resourceLoader.load(requestData.path());
            
            HttpResponse response = new HttpResponse(new DataOutputStream(out));
            if (resource == null) {
                response.send404("<h1>404 Not Found</h1>".getBytes());
            } else {
                response.send200(resource, requestData.path());
            }
        }
    }
}
```

### 5.3 HttpResponse
- 상태 코드 관리
- Content-Type 결정
- 헤더와 본문 전송

### 5.4 StaticResourceLoader
```java
public class StaticResourceLoader {
    private final String baseDirectory;

    public byte[] load(String path) throws IOException {
        if ("/".equals(path)) {
            path = "/index.html";
        }
        Path filePath = Paths.get(baseDirectory + path);
        return Files.exists(filePath) ? Files.readAllBytes(filePath) : null;
    }
}
```