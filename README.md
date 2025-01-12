# 학습한 내용 정리(1/6 월요일)

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
```
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

### 3.2 try-with-resource

```

try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {

```
- try 블록이 종료되면, in.close()와 out.close()가 자동으로 호출되어 리소스가 해제

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

---

# 학습한 내용 정리(1/7 화요일)

### **1. MIME 타입 매퍼 구현**

#### **구현 내용**
MIME 타입 매핑을 `ContentTypeMapper` 클래스로 분리하여 확장자 기반으로 Content-Type을 결정하도록 구현했습니다.

```java
public class ContentTypeMapper {
    private static final Map<String, String> MIME_TYPES = Map.of(
        "html", "text/html",
        "css", "text/css",
        "js", "application/javascript",
        "ico", "image/x-icon",
        "png", "image/png",
        "jpg", "image/jpeg",
        "jpeg", "image/jpeg",
        "svg", "image/svg+xml"
    );

    public static String getContentType(String path) {
        String extension = getExtension(path);
        return MIME_TYPES.getOrDefault(extension, "application/octet-stream");
    }

    private static String getExtension(String path) {
        int lastDot = path.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return path.substring(lastDot + 1).toLowerCase();
    }
}
```

#### **고민한 부분**
1. 확장자가 없는 경우 처리:
  - 초기에는 빈 문자열(`""`)을 반환했으나, 로그 기록 및 기본 Content-Type(`application/octet-stream`)을 반환하도록 개선.
2. 새로운 확장자 추가 시 유지보수:
  - `Map` 구조를 사용해 추가 작업을 단순화.
3. 정확성:
  - 확장자가 아닌 파일 내용 기반으로 MIME 타입을 판별해야 하는 상황에서는 Apache Tika 같은 라이브러리를 사용할 수도 있다고 학습.

#### **배운 점**
- MIME 타입은 브라우저가 리소스를 처리하는 데 필수적이며, 잘못 설정될 경우 CSS, JS, 이미지 등이 로드되지 않을 수 있음을 이해.
- `Map`과 정적 메서드를 활용한 매핑 관리의 효율성.

---

### **2. ThreadPoolExecutor와 리젝션 정책**

#### **구현 내용**
`Executors.newFixedThreadPool` 대신 `ThreadPoolExecutor`를 사용해 세부 설정 가능하도록 리팩토링했습니다.

```java
ExecutorService executorService = new ThreadPoolExecutor(
    CORE_POOL_SIZE,
    MAX_POOL_SIZE,
    KEEP_ALIVE_TIME,
    TimeUnit.SECONDS,
    new ArrayBlockingQueue<>(QUEUE_CAPACITY),
    new ThreadPoolExecutor.CallerRunsPolicy()
);
```

#### **고민한 부분**
1. **리젝션 정책(Rejection Policy)**:
  - `CallerRunsPolicy`: 큐가 가득 차면 작업을 호출한 스레드에서 실행.
  - `AbortPolicy`: 작업 거부 시 예외 발생.
  - `DiscardPolicy`: 작업을 무시.
  - `DiscardOldestPolicy`: 가장 오래된 작업을 제거.
  - 프로젝트의 안정성과 성능 균형을 위해 `CallerRunsPolicy` 선택.

2. **스레드 풀 크기 조정**:
  - **CORE_POOL_SIZE**와 **MAX_POOL_SIZE** 값을 적절히 설정하여 CPU 과부하를 방지.

#### **배운 점**
- `ThreadPoolExecutor`를 활용해 스레드 풀과 대기 큐를 세밀하게 설정할 수 있음을 학습.
- 리젝션 정책의 선택에 따라 시스템의 안정성, 성능, 응답 속도가 달라질 수 있다는 점을 이해.

---

### **3. 공백 문제와 HTTP 요청 파싱 개선**

#### **구현 내용**
HTTP 요청의 요청 라인(Request Line)을 파싱하는 로직에서 공백(`" "`) 처리와 예외 상황을 보완했습니다.

```java
String[] firstLineTokens = requestLine.split("\\s+");
if (firstLineTokens.length < 3) {
    throw new IOException("Invalid HTTP request: Malformed request line");
}
String method = firstLineTokens[0];
String path = firstLineTokens[1];
String httpVersion = firstLineTokens[2];
```

#### **고민한 부분**
1. **연속 공백 처리**:
  - 초기 코드에서 `split(" ")` 사용 시 연속된 공백을 적절히 처리하지 못할 가능성.
  - 개선 코드에서 `split("\\s+")` 사용하여 모든 공백(스페이스, 탭 등)을 처리.
2. **요청 라인의 길이 검증**:
  - HTTP 요청 라인이 `Method Path HTTP-Version` 형식을 따르도록 배열 길이를 검증.

#### **배운 점**
- HTTP 요청의 형식에 따라 파싱 로직을 설계해야 하며, 잘못된 요청을 감지하고 처리하는 로직이 중요함.
- 공백 처리에서 정규식을 사용하는 방법 학습.

---

### **4. 코드 리팩토링 및 유지보수성 고민**

#### **구현 내용**
- `HttpResponse` 클래스를 개선하여 Content-Type과 상태 코드 처리를 분리하고, 재사용성을 높임.
- MIME 타입 매핑, 스레드 풀 설정, HTTP 요청 파싱 등 여러 부분에서 유지보수성을 고려한 설계를 적용.

#### **고민한 부분**
1. 코드의 단일 책임 원칙(SRP)을 유지하면서도 클래스 간 의존성을 최소화.
2. 확장성과 유지보수성을 높이는 설계:
  - MIME 타입이나 리젝션 정책처럼 변경이 잦은 부분을 독립적으로 관리할 수 있도록 설계.

#### **배운 점**
- 클린 코드 원칙(SRP, DRY)을 적용한 설계의 중요성.
- 변경 가능성이 있는 부분을 분리하여 유지보수성을 높이는 방법 학습.

---

### **5. 배운 점**

1. **MIME 타입 관리의 중요성**:
  - 리소스(Content-Type) 설정이 올바르지 않으면 웹 페이지가 정상적으로 동작하지 않을 수 있다는 점.
  - 확장자 기반 MIME 타입 매핑의 간결성과 한계를 이해.

2. **ThreadPoolExecutor 활용**:
  - 스레드 풀 설정 및 리젝션 정책의 선택이 시스템 성능에 중요한 영향을 미친다는 점.
  - `CallerRunsPolicy`와 다른 정책들의 특성과 적합한 사용 시점 이해.

3. **HTTP 요청 파싱 개선**:
  - 정규식(`\\s+`)을 활용한 공백 처리의 필요성.
  - HTTP 요청의 구조와 파싱 시 주요 검증 로직.

4. **클린 코드 적용**:
  - 단일 책임 원칙(SRP)과 중복 제거(DRY)를 적용하여 유지보수성과 가독성을 높임.
  - 클래스 간의 의존성을 낮추는 설계 방법.

5. **트러블슈팅**:
  - CSS 및 이미지가 로드되지 않는 문제를 Content-Type 설정으로 해결.
  - 시스템 부하 시의 안정성 유지 방안 학습(스레드 풀, 리젝션 정책).

---

## Split 관련 추가 학습

#### **1. `split` 메서드의 기본 동작**

- **정의**:
  - Java의 `String.split(String regex)` 메서드는 **정규 표현식**을 기준으로 문자열을 분리하여 **문자열 배열**을 반환합니다.
  - 예:
    ```java
    String input = "Hello World";
    String[] parts = input.split(" ");
    ```
    - 결과: `["Hello", "World"]`

- **기본 특징**:
  - `split(" ")`은 정확히 **하나의 공백 문자(스페이스)**를 기준으로 문자열을 나눕니다.
  - 연속된 공백은 별도로 처리되지 않습니다.

---

#### **2. `\\s+`의 의미**

- **`\\s`**:
  - 정규식에서 `\\s`는 **공백 문자(whitespace character)**를 의미합니다.
  - 포함하는 문자:
    - 일반 스페이스 (` `)
    - 탭 (`\t`)
    - 개행 문자 (`\n`)
    - 캐리지 리턴 (`\r`)
    - 폼 피드 (`\f`)

- **`+`**:
  - `+`는 **1개 이상의 연속된 문자**를 매칭합니다.
  - 예:
    - `" "`, `"\t"`, `"\n"` → 매칭.
    - `"   "` → 매칭 (연속된 공백 포함).

- **`\\s+`**:
  - `\\s+`는 **1개 이상의 공백 문자**를 기준으로 문자열을 분리합니다.
  - 예:
    ```java
    String input = "GET    /index.html   HTTP/1.1";
    String[] parts = input.split("\\s+");
    ```
    - 결과: `["GET", "/index.html", "HTTP/1.1"]`

---

#### **3. `split("\\s+")`의 동작**

| 입력 문자열                     | 결과 배열                             |
|--------------------------------|-------------------------------------|
| `"GET /index.html HTTP/1.1"`   | `["GET", "/index.html", "HTTP/1.1"]` |
| `"GET    /index.html"`         | `["GET", "/index.html"]`            |
| `"\tGET\t/index.html\tHTTP/1.1"` | `["GET", "/index.html", "HTTP/1.1"]` |
| `"GET /index.html  "`          | `["GET", "/index.html"]`            |
| `"   "`                        | 빈 배열 `[]`                        |

- 연속된 공백이 하나로 처리되어, **공백 개수에 관계없이 동일한 결과**를 얻을 수 있습니다.
- 시작이나 끝에 공백이 있으면 자동으로 제거됩니다.

---

#### **4. `split(" ")` vs `split("\\s+")`**

| 특성                            | `split(" ")`                                  | `split("\\s+")`                              |
|--------------------------------|---------------------------------------------|---------------------------------------------|
| **기준**                        | 정확히 하나의 공백                         | 1개 이상의 공백(스페이스, 탭, 개행 포함)   |
| **연속된 공백** 처리             | 연속된 공백은 분리된 빈 문자열로 포함        | 연속된 공백은 하나로 간주                  |
| **탭(`\t`) 및 개행(`\n`) 처리** | 매칭되지 않음                              | 매칭되어 분리                              |
| **결과 배열 크기**               | 연속 공백의 개수에 따라 불필요한 요소 포함 가능 | 항상 필요한 요소만 포함                   |

- **예시 코드**:
    ```java
    String input = "GET    /index.html   HTTP/1.1";

    String[] result1 = input.split(" ");
    System.out.println(Arrays.toString(result1)); // ["GET", "", "", "", "/index.html", "", "", "HTTP/1.1"]

    String[] result2 = input.split("\\s+");
    System.out.println(Arrays.toString(result2)); // ["GET", "/index.html", "HTTP/1.1"]
    ```

---

#### **5. 실제 사용 사례**

1. **HTTP 요청 파싱**:
  - 요청 라인에 불필요한 공백이나 탭이 들어올 경우, `split("\\s+")`을 사용해 이를 깔끔하게 처리.

2. **CSV 또는 로그 파싱**:
  - 데이터가 불규칙하게 공백으로 구분된 경우(스페이스, 탭 혼합)에도 유용.
  - 예: `"INFO    2025-01-06  Event occurred" → ["INFO", "2025-01-06", "Event", "occurred"]`.

3. **유효성 검사**:
  - 사용자 입력 값에서 여러 공백을 제거하고 단어만 추출.
  - 예: `"    Hello    World   " → ["Hello", "World"]`.

---

# 학습한 내용 정리(1/8 수요일)

아래는 **1월 7일 이후**의 학습 및 구현 내용을 바탕으로 **학습일지**를 정리한 예시입니다.  
**(회원가입 관련 기능 중심, 학습 내용 / 구현 내용 / 고민 내용으로 나눠 기술)**

---

# 1월 8일 학습일지

## 1. 학습 내용

1. **HTTP GET 요청으로 회원가입 처리**
  - 브라우저에서 `/create?userId=...&password=...&name=...` 형태로 GET 요청을 보내, 서버가 파라미터를 파싱해 `User` 객체 생성 후 DB에 저장한다는 로직을 이해.
  - URL 인코딩/디코딩(예: `URLDecoder.decode(...)`) 개념 습득.

2. **클린 코드 원칙 적용**
  - 단일 책임 원칙(SRP)을 지켜 **RequestHandler**가 세부 로직을 갖지 않도록 설계.
  - `ApiRouter`를 두어 신규 API 추가 시 `RequestHandler`를 수정하지 않도록 구조화.
  - `if-else`를 최소화하고, `if`만 사용하여 분기 처리.

3. **메서드 분리와 명명 규칙**
  - `parseToUser` 메서드가 여러 로직(쿼리 파싱, User 생성)을 담당하던 부분을 **`parseQueryString`** 등으로 분리.
  - `ofSignup` vs `of` : 메서드 이름을 통해 객체 생성 의도를 명확히 표현하는 방법 학습.

---

## 2. 구현 내용

1. **회원가입 GET API**
  - **`UserCreationHandler`** 구현:
    - `canHandle(RequestData)`로 `/create` 경로인지 확인.
    - `handle(RequestData)`에서 `queryString` 파싱 → `User` 생성 → DB 저장.
  - `ApiRouter`: 여러 `ApiHandler`(예: `UserCreationHandler`)를 모아서, 경로에 맞는 API를 찾도록 구현.
  - `RequestHandler`에서는 `apiRouter.route(requestData)`로 API 핸들러 호출. 처리되지 않으면 정적 리소스로 넘어가는 구조.

2. **`User` 모델 변경**
  - 회원가입 시 이메일 필드는 null로 처리해서 User 객체 저장.
  - `User.of(...)` 메서드로 객체 생성 로직 통일.

3. **HTML 수정**
  - `registration/index.html`에서 `<button onclick="...">`를 통해 GET 요청으로 `/create?userId=...&password=...&name=...` 형태 전송.
  - `<input id="...">` 설정으로 JavaScript에서 `document.getElementById(...)`로 값 추출 가능.
  - JS에서 `encodeURIComponent(...)`로 URL 인코딩 처리.

4. **클린 코드 리팩토링**
  - `RequestHandler.run()`에서 API 처리(`handleApiRequest`)와 정적 처리(`handleStaticResource`)를 별도 메서드로 분리.
  - `if-else` 대신 `if`만 사용해 조건별 처리를 종료(`return`)하도록 작성.

---

## 3. 고민한 내용

1. **경로 확장자 없는 요청 시 Content-Type 문제**
  - `/registration` 경로가 **확장자 없이** 요청되면 기본 `application/octet-stream`이 적용되어 다운로드가 발생.
  - 해결책: `ContentTypeMapper`에서 확장자 없는 경우 `text/html`로 처리.

2. **메서드 명명**
  - `ofSignup` vs `of`: 메서드 이름으로 생성 의도를 충분히 표현해야 할지, 짧고 간결하게 유지해야 할지.
  - 최종적으로, 여러 생성 방법이 있을 수 있으면 `ofSignup` 등 구체적 이름이 유리. 단일 목적이면 `of`도 간결함.

3. **분기 로직**
  - `if-else` 구문을 제거하고 `if`만으로 처리 시, 코드 흐름이 이해하기 쉽지만 `return` 시점이 많아질 수 있음.
  - 여러 메서드로 세분화하고, `return`으로 분기 끝내는 방식을 적용해 코드 깊이를 낮춤.

4. **디코딩 로직**
  - URL 인코딩/디코딩 과정에서 발생하는 예외 처리나 잘못된 쿼리 파라미터 처리(예: `?userId=` 없이 들어오는 경우)를 어떻게 할지 고민.
  - 현재는 `null` 반환 → `400 Bad Request` 처리로 일단락.

---

# **1월 9일 학습일지**

## **1. 학습 내용**

1. **JSON 응답 처리**
  - 기존에 HTML로만 응답하던 방식을 확장해, **JSON 형식**으로 데이터를 반환하는 방법을 학습.
  - `sendJson` 메서드를 도입해 `Content-Type: application/json`으로 순수 JSON만 전송하도록 구현.

2. **HTTP 헤더와 바디 분리**
  - `HTTP/1.1 200 OK` 등 헤더가 JSON 바디에 섞이면 클라이언트 파싱 오류(`Unexpected token 'H'`) 발생.
  - **헤더와 바디를 정확히 구분**하고, 클라이언트 측에서 `response.json()`으로 올바르게 파싱하도록 수정.

---

## **2. 구현 내용**

1. **`LoadResult`에 `contentType` 필드 추가 (선택적)**
  - 일부 구현에서는 JSON/HTML 응답을 분기하기 위해 `LoadResult`에 `contentType` 저장.
  - `handleApiRequest`에서 분기하여 `sendJson` vs `send200` 결정.

2. **`HttpResponse.sendJson` 수정**
  - 순수 JSON만 바디에 담아 전송하도록 수정.
  - 헤더 정보가 섞이지 않도록 `writeHeader` 호출 시에도 `application/json`을 명시, 클라이언트는 `response.json()`으로 파싱 가능.

3. **토큰 기반 예외 처리**
  - `UserDataHandler`에서 `token == null`인 경우 `TOKEN_MISSING`,
  - `TokenStore`에서 못 찾은 경우 `TOKEN_NOT_FOUND` 예외.
  - 예외 발생 시 HTML로 에러 노출 대신 JSON 또는 별도 흐름으로 처리(상위 레벨에서 예외 잡기 가능).

---

## **3. 고민한 내용**

1. **“Unexpected token 'H'” 파싱 오류**
  - 클라이언트(`fetch(...).json()`)에서 JSON으로 파싱하는데, 서버 응답에 **HTTP 헤더**가 섞여버림.
  - **원인**: 단일 메서드(`sendJson`)에서 헤더+바디를 구분하지 않은 채 전송한 경우, 또는 응답에 추가 정보가 들어간 경우.
  - **해결**: 오직 JSON 바디만 전송, 브라우저 개발자 도구(Network)에서 응답 형태가 순수 JSON인지 확인.

2. **화면에서 기존 입력값 복원 실패**
  - 위 파싱 오류로 인해 `fetchUserData(token)`이 `SyntaxError` 발생.
  - JSON 데이터를 제대로 받지 못하니 `userData`가 `null`이 되어 `<input>`에 값을 세팅하지 못함.
  - **해결**: 순수 JSON 반환 후, 클라이언트 로직(`await response.json()`)이 정상 동작 → 기존 값 복원됨.

3. **Handler 별 응답 형식 차이**
  - `UserCreationHandler`: HTML 리다이렉트 용도.
  - `UserDataHandler`: JSON 응답 용도.
  - **처리 방식**:
    - `LoadResult` 혹은 `contentType`으로 분기,
    - API 라우터에서 헤더 타입을 지정 후 `handleApiRequest` 메서드가 판단.

4. **HTTP와 유사한 구조**
  - 프로젝트가 순수 자바이지만, 스프링 MVC처럼 **`ApiRouter`**, **핸들러 분리**(Controller 유사), **에러 코드**(ExceptionHandler 유사) 등 구조가 유사해진 점 확인.
  - 핸들러 추가 시마다 **“HTML vs JSON vs 기타”** 구분 로직 고민 필요.

---

아래는 **1월 9일 학습일지** 이후 작업(질문·답변 과정)에서 진행된 내용을 정리한 학습일지입니다. 기존과 마찬가지로 **학습 내용**, **구현 내용**, **고민한 내용**으로 구분했습니다. (1월 9일 학습일지에 이미 언급된 사항은 생략하고, 그 이후 추가/변경된 내용만 담았습니다.)

---

# **1월 10일 학습일지**

## **1. 학습 내용**

1. **쿼리 파라미터 누락/없음 처리 방식**
  - 기존에는 `extractQueryString`에서 예외(`UserCreationException`)를 던졌고, 테스트에서 JSON 응답이 아닌 예외 발생으로 실패가 뜸.
  - 이를 개선해 **쿼리 문자열이 없는 경우**나 **필수 파라미터 누락** 시에도 **JSON 응답**을 반환

2**테스트 독립성**
  - **DB 초기화**(`Database.clear()`)를 통해 테스트 간 상태 공유를 방지하는 방법.
  - 각 테스트 메서드 실행 전/후 `clear()`를 호출하거나, `@BeforeEach`와 `@AfterEach`에 추가해 독립성 보장.

3**테스트 코드 수정**
  - 기존 테스트에서 “쿼리 문자열 없을 시 `null` 반환” 로직을 삭제하고, **JSON 응답**을 검증하도록 변경.
  - `SIGNUP-03` 응답이 오는지 확인하는 방식으로 테스트 시나리오 조정.

---

## **2. 구현 내용**

1. **`UserCreationHandler` 수정**
  - `canHandle`
    - 예외 제거, `boolean`만 반환.
  - `extractQueryString`
    - 로직은 유지하되, 발생한 예외는 `handleSignupFailure`에서 잡아 **JSON 응답**으로 전환.
  - `handleSignupFailure`:  
    - 스위치 대신 `getCodeForError` 메서드에서 `ErrorCode`를 `"SIGNUP-01"`, `"SIGNUP-02"`, `"SIGNUP-03"`, `"UNKNOWN_ERROR"`로 매핑.
  - 성공 시
    ```json
    { "isSuccess": true, "code": null, "message": null, "data": null }
    ```
    실패 시 `SIGNUP-01/02/03`과 함께 `ErrorCode` 메시지 응답.

2. **테스트 코드 수정**
  - “쿼리 문자열 없을 때” & “필수 파라미터 누락 시” → `SIGNUP-03` 응답 확인.
  - 기존에 **`assertThat(loadResult).isNull()`** 했던 부분을 **JSON 응답** 비교로 변경.

3. **`Database.clear()`**
  - 테스트 클래스에 `@BeforeEach`나 `@AfterEach`를 달아 `Database.clear()`를 호출,
  - 데이터베이스를 항상 빈 상태로 만들어 테스트 독립성 확보.

---

## **3. 고민한 내용**

1. **예외 처리 vs JSON 응답**
  - 예외를 던졌을 때 바로 종료되므로, 어디서 JSON 응답을 생성할지 결정이 필요.
  - 현재 방식: `try { ... } catch (UserCreationException e) { return handleSignupFailure(e); }`
  - 던져진 예외를 핸들러 내부에서 잡아 **JSON 응답**으로 변환하는 구조.

2. **‘필수 파라미터 없음’과 ‘쿼리 파라미터 없음’ 예외처리**
  - 모두 `INVALID_USER_INPUT`로 보고 `SIGNUP-03` 코드 부여.
  - 필요하다면 “쿼리 없음” vs “필드 누락”을 세분화할 수도 있으나, 현재는 단일 코드로 처리.

3. **데이터베이스 상태 공유 문제**
  - 테스트들이 `Database`의 static 필드에 영향을 주고, 다른 테스트에 반영됨 → 각 테스트 끝에 `clear()`.
  - “테스트 독립성” 확보가 우선.

4. **확장성 고민**
  - 아이디/닉네임 중복 외에 이메일 중복 등 추후 요구사항이 생기면, `validateUser`와 `ErrorCode`에 추가하여 확장.
  - 응답 형식(`data`)에 추가 정보를 넣어줄 수도 있음.

---