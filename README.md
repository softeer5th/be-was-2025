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

---

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

#### **6. 정리**

- **`split("\\s+")`은 공백 처리에 가장 안전하고 유연**:
  - 스페이스뿐 아니라 탭, 개행 문자도 처리.
  - 연속된 공백을 하나로 간주하여 불필요한 빈 문자열 제거.
- 일반적인 HTTP 요청 파싱이나 공백이 포함된 데이터 처리에는 `split("\\s+")`이 적합합니다. 🚀