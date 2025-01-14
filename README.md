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
  - `RequestHandler`에서는 `apiRouter.route(httpRequest)`로 API 핸들러 호출. 처리되지 않으면 정적 리소스로 넘어가는 구조.

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

# **1월 13일 학습일지**

---

## **1. 학습 내용**

### 브라우저에서 서버에 요청을 보내고 응답을 받아 화면에 출력하기 까지의 과정

## 1. URL 입력과 요청 준비

1. **URL 입력**

   사용자가 브라우저의 주소창에 `https://www.example.com/` 같은 URL을 입력

2. **브라우저 내부 처리**
    - **URL Parsing**: 브라우저는 입력된 URL을 분석하여 프로토콜(HTTP/HTTPS), 호스트명(도메인), 경로(path), 쿼리(query string) 등을 구분
    - **캐시 확인**: 브라우저는 가장 먼저 내부 DNS 캐시, 그리고 OS 계층의 호스트 캐시(hosts 파일, 로컬 DNS 캐시)를 확인한다. 이미 해당 도메인의 IP 주소를 알고 있다면 DNS 조회 과정을 생략할 수 있다.
    - **프로토콜과 포트 결정**: 만약 프로토콜이 `https://`라면 기본 포트는 443, `http://`라면 기본 포트는 80으로 인지한다.

---

## 2. DNS 조회 과정(도메인 -> IP 주소)

브라우저가 로컬 캐시에서 해당 도메인의 IP 주소를 찾지 못했다면, DNS 서버에 질의하여 IP 주소를 얻어야 한다. 이를 **DNS 쿼리**라고 한다.

1. **브라우저(Stub Resolver)**
    - 브라우저 또는 OS의 Stub Resolver(간단한 DNS 클라이언트)가 로컬 DNS 서버(보통 ISP나 회사 내부 DNS 서버)에 해당 도메인(`www.example.com`)의 A 레코드(IPv4) 또는 AAAA 레코드(IPv6)를 요청한다.
    - `gethostbyname()` 또는 `getaddrinfo()`와 같은 OS 레벨의 함수가 내부적으로 Stub Resolver 역할을 한다.
2. **로컬 DNS 서버(Recursive Resolver)**
    - 로컬 DNS 서버(일반적으로 ISP에서 제공하거나 회사 내부에 존재)는 **재귀(Recursive) 방식**으로 DNS 정보를 질의
    - 만약 이 로컬 DNS 서버에 캐시가 있다면 캐시 결과를 바로 반환하고, 없다면 아래 단계를 진행
    1. **Root 서버 조회**
        - 로컬 DNS 서버는 먼저 Root Name Server에 `.com` 도메인을 관리하는 TLD 서버의 주소를 물어본다. (Root 서버 주소는 일반적으로 DNS 소프트웨어에 하드코딩되어 있음)
    2. **TLD 서버 조회**
        - Root 서버로부터 `.com` TLD 서버의 주소를 받아온 뒤, `.com` TLD 서버에 `example.com`에 대한 DNS 정보를 물어본다.
    3. **권한 서버(Authoritative Name Server) 조회**
        - TLD 서버로부터 `example.com`을 관리하는 권한(Authoritative) DNS 서버 주소를 받아온다.
        - 이후, 권한 DNS 서버에 `www.example.com`의 A(또는 AAAA) 레코드를 요청한다.
    4. **IP 주소 반환**
        - 권한 DNS 서버가 `www.example.com`의 실제 IP 주소(예: `93.184.216.34`)를 로컬 DNS 서버에 반환한다.
    5. **DNS 응답 캐싱**
        - 로컬 DNS 서버는 받은 결과를 TTL(Time To Live) 시간만큼 캐시에 저장한다. Stub Resolver(브라우저/OS)에게 IP 주소를 응답한다.
3. **브라우저(Stub Resolver) 수신**
    - 브라우저는 최종적으로 로컬 DNS 서버에서 반환된 IP 주소 정보를 받는다.
    - 이제 브라우저는 `www.example.com`에 대한 IP 주소를 알게 되었으므로, TCP/UDP 레벨에서 실제 통신을 시도할 준비를 한다.

---

## 3. TCP/UDP 소켓 연결(전송 계층)과 HTTPS(SSL/TLS) 핸드셰이크

### 3.1 TCP 핸드셰이크 (HTTP의 경우)

1. **소켓 생성**
    - 브라우저는 해당 IP 주소와 포트(기본 80)로 통신하기 위해 OS에 소켓을 생성하도록 요청한다.
    - 이때 전송 프로토콜은 **TCP**이다(HTTP는 TCP 기반).
2. **3-Way Handshake**
    - **SYN**: 클라이언트(브라우저)는 서버 IP와 포트 80으로 SYN 패킷을 보낸다.
    - **SYN+ACK**: 서버는 SYN 패킷을 받고, SYN+ACK 패킷을 클라이언트에게 보낸다.
    - **ACK**: 클라이언트는 SYN+ACK를 받고, ACK 패킷을 서버에게 보낸다.
    - 이 과정을 통해 **논리적 연결이 확립**된다.

### 3.2 TLS 핸드셰이크 (HTTPS의 경우)

1. **TCP 3-Way Handshake**
    - HTTPS에서도 우선 TCP 연결(3-Way Handshake)이 선행된다(포트 443).
2. **TLS Handshake**
    - 클라이언트(브라우저)와 서버가 서로 **암호화 통신**을 위해 TLS(또는 SSL) 프로토콜을 수행
    - **Client Hello**: 클라이언트가 사용 가능한 TLS Version, Client가 지원하는 암호화 방식, Client Random Data(클라이언트에서 생성한 난수로 대칭키를 만들 때 사용), Session ID, SNI(서버명) 가 포함하여 서버에게 알린다.
        - 매번 연결할 때마다 Handshake 과정을 진행하는 것은 비효율적이니 최초 한번 전체 Handshake 과정을 진행하고 Session ID를 가진다. 후에는 이 Session ID를 사용해서 위 과정을 반복해서 진행하지 않는다.
    - **Server Hello**: 서버가 어떤 암호화 방식을 사용할지 결정 후, TLS Version, 암호화 방식(Client가 보낸 암호화 방식 중에 서버가 사용 가능한 암호화 방식을 선택), Server Random Data(서버에서 생성한 난수, 대칭키를 만들 때 사용), SessionID(유효한 Session ID)를 클라이언트에게 보낸다.
    - **Server Certificate**: 서버의 인증서를 클라이언트에게 보내는 단계로 필요에 따라 CA의 Certificate도 함께 전송
    - **Server Hello Done:** 메시지 모두 보냄
    - **서버 인증서 검증**: 브라우저는 CA(인증서 발급 기관)의 루트 인증서 등을 통해 서버의 인증서를 검증
    - **Pre-Master Secret 교환**: 클라이언트가 세션 키를 생성하기 위한 인증서가 무결한지 검증 되었으면 클라이언트의 난수와 서버의 난수를 조합하여 대칭키를 생성한다.그리고 대칭키를 서버의 공개키로 암호화
    - **세션키(대칭키) 생성**: 클라이언트와 서버는 이후 대칭키(세션 키)를 공유하여, 실제 데이터 교환은 대칭키로 암호화해 송수신한다.
    - **Handshake 완료**: 보안 채널이 설정되면 TLS 핸드셰이크가 끝나고, 이후 HTTPS 데이터를 전송한다.

---

## 4. 데이터 링크/네트워크 계층: 라우팅 & 전송

### 4.1 이더넷/무선 LAN/ARP 과정(근거리)

1. **ARP(IPv4) 혹은 NDP(IPv6)**
    - Address Resolution Protocol로 IP주소에 맞는 물리적인 주소 즉, MAC주소를 가지고 오는 프로토콜이다.
    - 출발지(클라이언트)와 게이트웨이(라우터)가 같은 서브넷 상에 있는 경우, MAC 주소를 알아야 실제 이더넷 프레임(또는 Wi-Fi 프레임)을 전송할 수 있다.
    - ARP(IPv4) 또는 NDP(IPv6)를 통해 “이 IP를 가진 장치의 MAC 주소가 뭐지?”라는 질문을 하고, 응답을 통해 MAC 주소를 얻는다.
        - 이를 브로드캐스트라고 하며 목적지의 MAC주소를 모르기 때문에 모두에게 요청
    - 얻은 MAC 주소로 **이더넷 프레임**(L2 계층 헤더)에 목적지 MAC을 설정해 패킷을 전송한다.
2. ARP 동작 과정 총 정리
    - 1) 송신자는 목적지 IP Address를 지정해 패킷 송신
    - 2) IP 프로토콜이 ARP 프로토콜에게 ARP Request 메시지를 생성하도록 요청= ARP 요청 메시지 (송신자 물리주소, 송신자 IP주소, 00-00-00-00-00, 수신자 IP주소)
    - 3) 메시지는 2계층으로 전달되고 이더넷 프레임으로 Encapsulation 됨= 송신자 물리주소를 발신지 MAC 주소로 설정, 수신자 물리주소를 브로드캐스트 주소로 지정
    - 4) 모든 호스트와 라우터는 프레임을 수신 후 자신의 ARP 프로토콜에게 전달
    - 5) 목적지 IP Address가 일치하는 시스템은 자신의 물리주소를 포함하고 있는 ARP Reply 메시지를 보냄= 자신의 물리주소를 포함하는 응답 메시지
    - 6) 최초 송신 측은 지정한 IP Address에 대응하는 물리주소를 획득
    - cf) ARP 요청은 브로드캐스트, ARP 응답은 유니캐스트
3. **Frame(2계층) 전송**
    - 클라이언트 PC에서 라우터(게이트웨이)로, 또는 스위치 등을 거쳐 물리적으로 패킷이 전달된다.
        - https://egstory.net/edge-study/tech-lesson/aos-cx-switching/565/
    - 무선일 경우엔 Wi-Fi(802.11) 프로토콜을 통해 AP(Access Point)로 전송된다.

### 4.2 IP 레벨 라우팅(원거리)

1. **라우터의 역할(3계층) - IP 주소 사용**
    - 이름 그대로 네트워크와 네트워크 간의 경로(Route)를 설정하고 가장 빠른 길로 트래픽을 이끌어주는 네트워크 장비
    - 패킷의 **IP 헤더**를 확인해 목적지 IP 주소를 기준으로 **라우팅 테이블**을 참조하여 다음 홉(Next Hop)으로 전송한다.
2. **인터넷 백본 / ISP 라우팅**
    - 여러 ISP(인터넷 서비스 제공 업체)나 백본 망(Backbone Network)들이 BGP, OSPF 등의 프로토콜로 연결되어, 전 세계에 분산된 라우터들이 목적지 IP 주소가 어디에 있는지를 찾는다.
    - 라우팅 프로토콜
        - **Static Routing(정적 라우팅)**
            - 관리자가 네트워크에 대한 경로 정보를 직접 지정하여 라우팅
            - 네트워크 변화가 빈번하거나 등록할 네트워크 수가 많을 경우 경로 설정을 변경하기 어렵다
        - **Dynamic Routing(동적 라우팅)**
            - 대규모 네트워크에 사용하며 라우터 간의 변경된 네트워크에 대한 정보를 자동으로 교환하여 라우팅
            - outing table을 자동으로 작성하여 관리자의 초기 설정만 필요

      <img src="https://github.com/user-attachments/assets/c6f9f968-d605-40ac-beba-79a125a82d35" alt="라우팅프로토콜" width="300">


3. **최종 목적지 근처 라우터**
    - 목적지 서버(예: `93.184.216.34`)가 속한 데이터센터나 호스팅 업체의 네트워크로 패킷이 도달한다.
    - 내부 라우팅을 통해 실제 서버가 위치한 서브넷으로 전달된다.
4. **서브넷 내부 전달**
    - 마지막으로 해당 서버가 있는 서브넷 내의 스위치, 라우터를 거쳐 서버의 NIC(Network Interface Card)로 전달된다.

---

## 5. 서버 측 처리

1. **서버 NIC 수신**
    - 서버 네트워크 카드(NIC)는 물리 계층(이더넷) 프레임을 수신하고, 이더넷 헤더/트레일러를 제거한 뒤 IP 패킷을 OS 네트워크 계층으로 넘긴다.
2. **IP 계층 확인**
    - 목적지 IP가 서버 자신이고, TCP/UDP 포트가 열려 있는 경우라면 해당 프로세스로 전달한다.
3. **TCP 계층 처리**
    - 이미 3-Way Handshake가 완료된 상태라면, 해당 소켓으로 데이터(HTTP 요청)가 들어온다.
4. **HTTPS라면 TLS 계층 복호화**
    - 만약 HTTPS라면, TLS 계층에서 암호화된 데이터를 복호화하여 실제 HTTP 요청 메시지를 추출한다.
5. **웹 서버(애플리케이션 서버) 처리**
    - NGINX, Apache, IIS 등의 웹 서버 소프트웨어가 요청 헤더, URI 경로, 메소드(GET/POST 등), 쿠키/세션 정보 등을 파싱한다.
    - 정적 파일 서비스인 경우, 서버는 파일 시스템에서 해당 리소스를 찾아 응답한다.
    - 동적 서비스(예: PHP, Node.js, Python 등)인 경우, 애플리케이션 서버에 요청을 전달해 필요한 로직을 처리하고 DB 연동 등을 수행해 결과를 만든다.

---

## 6. 서버에서 클라이언트로 응답 전송

1. **HTTP 응답 생성**
    - 웹 서버(또는 애플리케이션 서버)는 HTTP 응답 헤더와 바디(HTML, JSON, 파일 등)를 구성한다.

2. **TLS 암호화(HTTPS)**
    - HTTPS 연결이라면, 응답 바이트 스트림을 TLS 계층에서 대칭키로 암호화한다.
3. **TCP/IP 스택을 통한 전송**
    - 서버 OS의 TCP 계층은 보낼 데이터를 세그먼트로 쪼개어 IP 계층으로 전달한다.
    - IP 계층은 목적지 IP(클라이언트 IP)로 라우팅 정보를 확인하고, 이더넷(또는 기타 L2) 헤더를 씌워 물리 인터페이스로 전송한다.
4. **인터넷 라우팅 역방향**
    - 인터넷을 통해 역방향으로 라우팅되어, 클라이언트(브라우저)에게 패킷이 도달한다.

---

## 7. 클라이언트(브라우저)에서 응답 처리 & 렌더링

1. **수신 및 TCP 계층 재조립**
    - 클라이언트의 네트워크 카드가 패킷을 수신하고 OS TCP 스택이 세그먼트를 재조립한다.
    - HTTPS라면 TLS 계층에서 복호화하여 원본 HTTP 응답 데이터를 얻는다.
2. **브라우저 렌더링 엔진**
    - HTTP 응답 헤더와 바디를 분석한다.
    - 응답이 HTML이라면 DOM 트리를 구성하고, 그 안에 `<link>`, `<script>`, `<img>` 태그 등이 있으면 추가 리소스 요청을 다시 시작한다(CSS, JS, 이미지 파일 등).
3. **CSS, JS, 이미지 등 동시 요청**
    - 브라우저는 필요한 리소스를 병렬로 여러 TCP 연결을 통해 가져오려고 시도한다(HTTP/2, HTTP/3에서는 단일 연결에서도 멀티플렉싱).
4. **최종 렌더링**
    - DOM 트리 + CSSOM 트리를 결합하여 렌더 트리를 만들고, 레이아웃 계산 및 페인트 과정을 거쳐 최종 화면에 웹 페이지가 표시된다.

---

## **2. 구현 내용**

1. **POST 요청 전환**
    - 기존 GET 요청에서 쿼리 파라미터를 사용하던 방식을 POST 요청으로 전환.
    - 요청 데이터는 JSON 형식으로 전달, `HttpRequest.body()`에서 JSON 데이터를 파싱하여 처리.

2. **리다이렉션 구현**
    - `HttpResponse.sendRedirect(location)` 메서드에서 `302 Found` 응답 처리.
    - `HttpStatus` Enum 추가로 상태 코드와 메시지를 관리.

3. **UserCreationHandler 수정**
    - JSON 요청 바디에서 사용자 정보를 추출(`createUserFromBody`).
    - 입력값 검증 실패(`필수 필드 누락, 중복된 아이디/닉네임`) 시, 실패 응답 JSON(`SIGNUP-01`, `SIGNUP-02`, `SIGNUP-03`) 반환.
    - 성공 시 `302 Found` 응답으로 리다이렉션 처리.

4. **JSON 유틸리티 확장**
    - JSON 문자열을 `Map<String, String>`으로 변환하는 기능(`fromJson`) 추가.
    - JSON 문자열 생성 유틸리티(`toJson`) 개선:
        - `CommonResponse`와 `Map` 객체 모두 지원.
        - 특수문자 이스케이프 처리.

---

## **3. 고민한 내용**

1. **GET vs POST 요청**
    - GET 요청은 쿼리 파라미터 노출로 보안 이슈 발생 가능.
    - POST 요청으로 전환 시 데이터가 요청 바디에 담기지만, 파싱 로직 추가 및 요청 헤더 설정 필요.

2. **리다이렉션 vs JSON 응답**
    - 성공 시 리다이렉션(302)과 실패 시 JSON 응답 방식 혼용으로 코드 복잡도 증가.
    - API 구조를 일관되게 유지하려면 모두 JSON 응답으로 처리하는 방안도 고려.

3. **HTTP 상태 코드 관리**
    - HTTP 상태 코드의 확장성과 가독성을 위해 `HttpStatus` Enum 도입.
    - Enum을 통해 상태 메시지와 코드 간 매핑을 관리하므로, 코드 수정 시 중앙에서 일괄 관리 가능.

4. **JSON 유틸리티 확장**
    - 특수문자 이스케이프와 복구 처리의 필요성 고민.
    - JSON 파싱을 라이브러리 없이 구현 시, 간단한 데이터 구조에서는 `Map` 변환으로 충분하지만 복잡한 구조에서는 한계.

---
