# be-was-2025
코드스쿼드 백엔드 교육용 WAS 2025 개정판

# 학습 기록
[level 1](https://github.com/softeer5th/backend-page/wiki/%ED%95%9C%EC%A4%80%ED%98%B8_week1_day1)

[level 2, 3](https://github.com/softeer5th/backend-page/wiki/%ED%95%9C%EC%A4%80%ED%98%B8_week1_day2)

# Level 1, 2

## 학습해야 하는 내용
- LogBack
- HTTP 파싱

## 구현해야 하는 것
- HTTP 파싱
- ExecutorService 사용하도록 변경

## 도메인 모델 - Level 1, 2
```mermaid
classDiagram
    class WebServer {
        - logger: Logger
    }

    class RequestHandler {
        - connectionSocket: Socket
    }

    class HttpRequest {
        - method: HttpMethod
        - uri: String
        - parameters: Map<String, String>
        - protocol: String
        - headers: Map<String, String>
        - body: String
    }

    class HttpResponse {
        - logger: Logger
        - protocol: String
        - statusCode: StatusCode
        - headers: Map<String, String>
        - cookies: Map<String, String>
        - body: byte[]
    }

    WebServer "1" -- "0..*" RequestHandler : creates
    RequestHandler "1" -- "1" HttpRequest : uses
    RequestHandler "1" -- "1" HttpResponse : uses
```

# Level 3

## 현재 문제점

- HTTP Request Line 의 URI 와 정적 폴더 구조가 강결합되어있다.
    - GRASP의 컨트롤러 패턴을 통해 적절한 URI 를 탐색해서 반환하는 구조가 필요하다.
    - HTTP 메시지와 리소스 간 느슨한 결합을 만들어야 한다.
- 하지만 정적 리소스도 서빙해야 한다.
    - CSS, svg파일과 같은 리소스도 서빙할 수 있어야 한다.
- 우선순위를 설정해야 한다.
    1. 동적 리소스
    2. 정적 리소스
    3. 404 Not Found

## 도메인 모델 - level 3
```mermaid
classDiagram
    class WebServer {
        - logger: Logger
    }

    class RequestHandler {
        - connectionSocket: Socket
    }

    class HttpRequest {
        - method: HttpMethod
        - uri: String
        - parameters: Map<String, String>
        - protocol: String
        - headers: Map<String, String>
        - body: String
    }

    class HttpResponse {
        - logger: Logger
        - protocol: String
        - statusCode: StatusCode
        - headers: Map<String, String>
        - cookies: Map<String, String>
        - body: byte[]
    }
    
    class ServletManager {
        - Servlets: Map<String, Servlet>
    }
    
    class Servlet {
        
    }
    
    class User{
        - userId: String
        - password: String
        - name: String
        - email: String
    }
    
    class Database{
        - users: Map<String, User>
    }

    WebServer "1" -- "0..*" RequestHandler : creates
    WebServer *-- "1" ServletManager : create
    RequestHandler "1" -- "1" HttpRequest : uses
    RequestHandler "1" -- "1" HttpResponse : uses
    RequestHandler --> ServletManager : use
    ServletManager *-- Servlet
    Servlet --> User
    Servlet "0..*" -- "1" Database
    Database "1" -- "0..*" User
```

## 개선해야 할 점

- [x] 서블릿들에 발생한 수많은 중복 코드들을 제거해야 함
  - 가짜 중복으로 봐야 함
- [ ] 쿼리 파라미터나 헤더가 여러개 있을 경우의 처리가 필요할듯.
- [x] read 시 파일의 끝에 도달했는지 확인하면서 읽어야 할듯
  - 버퍼 공간이 모잘라서 중간에 끊길수도
- [x] Http 메시지의 헤더는 대소문자를 가리지 않는다. 해당 부분 처리 필요.
- [x] WebServer 의 Main 메소드가 지나치게 무거운 문제
- [ ] 테스트 커버리지가 낮음
- [x] 현재 Servlet 클래스가 지나치게 많아 패키지 구조 이해에 어려움을 주는 중
  - ContentType Enum 처럼, 리플렉션을 이용해 루프를 돌면서 URL을 매핑하는건 어떨까?

# Level 4
## 현재 문제점
- [x] 현재 Servlet 클래스가 지나치게 많아 패키지 구조 이해에 어려움을 주는 중
  - ContentType Enum 처럼, 리플렉션을 이용해 루프를 돌면서 URL을 매핑하는건 어떨까?
- [x] 추후에 게시판을 만들텐데, 게시글의 id를 구분하려면 Path Variable이 필요할텐데, 우아하게 처리하는 방법 없을까?
- [x] Http 메시지의 헤더는 대소문자를 가리지 않는다. 해당 부분 처리 필요.
- [ ] 쿼리 파라미터나 헤더가 여러개 있을 경우의 처리가 필요할듯.
- [ ] 테스트 커버리지가 낮음


## 도메인 모델 - level 4
```mermaid
classDiagram
    class WebServer {
        - logger: Logger
    }

    class RequestHandler {
        - connectionSocket: Socket
    }

    class HttpRequest {
        - method: HttpMethod
        - uri: String
        - parameters: Map<String, String>
        - protocol: String
        - headers: Map<String, String>
        - body: String
    }

    class HttpResponse {
        - logger: Logger
        - protocol: String
        - statusCode: StatusCode
        - headers: Map<String, String>
        - cookies: Map<String, String>
        - body: byte[]
    }
    
    class ServletManager {
        - Servlets: Map<String, Servlet>
    }
    
    class User{
        - userId: String
        - password: String
        - name: String
        - email: String
    }
    
    class Database{
        - users: Map<String, User>
    }

    WebServer "1" -- "0..*" RequestHandler : creates
    WebServer *-- "1" ServletManager : create
    RequestHandler "1" -- "1" HttpRequest : uses
    RequestHandler "1" -- "1" HttpResponse : uses
    RequestHandler --> ServletManager : use
    ServletManager *-- DispatcherServlet
    DispatcherServlet --> HandlerMapping
    DispatcherServlet --> HandlerAdaptor
    DispatcherServlet "1" -- "0..*" Controller
    Controller "0..*" -- "1" Database
    Controller --> User
    Database "1" -- "0..*" User
```

## 남은 문제점
- [ ] setBody로 한글이 작성되지 않는 문제 - UTF-8, ISO 관련 인코딩 문제일수도
    - Content-Type: text/html; charset=utf-8 로 해결
- [x] Create 서블릿"Location" 문자열 하드코딩되어있음
- [x] Header ": " 공백 지우기
- [x] uri 보단 path 라는 변수명을 사용하는게 좋아보임
- 너무 늦었다. 테스트 코드 작성한 뒤 리팩토링에 대한 두려움 제거되면 시도
- [x] Request 요청 시 BufferedReader 사용하면 안됨
- 문자열 기반이라
- byte[] 이진 데이터 받을 땐 Reader Writer 사용 불가
- [ ] 예외 처리의 혼재
- 동적 리소스 서빙 예외 처리
- 정적 리소스 서빙 예외 처리
- HTTP 메시지 파싱 예외 처리
- [ ] 에러 페이지를 파일로 만들어서 그냥 파일로 찾아서 서빙하는게 좋을듯
- [ ] 리소스 생성 시 반복해서 등장하는 파일 입력 받은 후 Response에 기록하는 코드를 제거할 방법
- [ ] 스프링과 아키텍처가 지나치게 유사한 문제 해결
- [ ] Ambiguous Mapping 검증
-  /test/{id} 와 /test/hello 는 충돌해야 함
- plain 도 렌더링이 되는 이유?



# Level 5
- [x] 예외 처리의 혼재
  - 동적 리소스 서빙 예외 처리 -> 디스패처 서블릿에서 처리
  - HTTP 메시지 파싱 + 정적 리소스 서빙 예외 처리 -> 서블릿 매니저에서 처리
- [x] 쿠키와 세션을 이용한 로그인/로그아웃 구현
- [x] 폼에 아무것도 입력하지 않은 칸이 있을 시 처리 방식
  - 단순히 400 Bad Request 처리
- [x] 헤더의 처리 방식
    - 쿠키와 MimeType 등 특이 헤더에 대한 처리
    - 현재 쿠키에 대해서만 처리


## 도메인 모델 - level 5
- Level 4와 달라진 것 없음


## 추가적으로 배운 것
- StringBuilder vs Collectors.joining
    - Collectors.joining 은 내부적으로 CharSequence 를 사용한다.
      - 가변 문자 배열을 이용하여 크기를 직접 늘려가며 join 한다.
    - StringBuilder 도 내부적으로 CharSequence 를 사용한다.
      - 가변 문자 배열을 이용하여 크기를 직접 늘려가며 append한다.
    - 따라서 둘의 성능에 큰 차이는 없다.

## 남은 문제점
- [ ] 에러 페이지를 파일로 만들어서 그냥 파일로 찾아서 서빙하는게 좋을듯
- [ ] 리소스 생성 시 반복해서 등장하는 파일 입력 받은 후 Response에 기록하는 코드를 제거할 방법
- [ ] 스프링과 아키텍처가 지나치게 유사한 문제 해결
- [ ] Ambiguous Mapping 검증
  -  /test/{id} 와 /test/hello 는 충돌해야 함


# Level 6
## 현재 문제점
- [x] 핵심 비즈니스 로직에 대한 테스트 코드 작성
- [x] 생성자 호출 시 반드시 호출되어야 하는 파싱 로직 때문에, stubbing 이 어려워짐
  - 지나치게 무거워진 HttpRequest 생성자의 생성 로직을 분해할 필요성 느낌
  - 객체의 생성 책임과 파싱 책임의 분리 -> 팩토리
  - 매개변수와 파라미터를 동적으로 추가 가능하게 작성 -> 빌더 패턴
- [x] 서블릿 매니저가 서빙 뿐만 아니라 예외처리까지 하는 문제
  - ExceptionHandler 생성 -> 스프링과 겹침. 포기.
- [x] 에러 페이지를 파일로 만들어서 그냥 파일로 찾아서 서빙하는게 좋을듯
  - 스프링의 forward 와 겹침, 포기.
- [ ] 6단계 작성
  - 커스텀 태그 생성 및 StringBuilder 로 동적 HTML 응답 만들기
- [ ] 리소스 생성 시 반복해서 등장하는 파일 입력 받은 후 Response 에 기록하는 코드를 제거할 방법
- [ ] 스프링과 아키텍처가 지나치게 유사한 문제 해결
- [ ] Ambiguous Mapping 검증
  -  /test/{id} 와 /test/hello 는 충돌해야 함

## 도메인 모델 - level 6
```mermaid
classDiagram
    class WebServer {
        - logger: Logger
    }

    class RequestHandler {
        - connectionSocket: Socket
    }

    class HttpRequestFactory{
    }
    
    class CookieFactory{
        
    }

    class HttpRequest {
        - method: HttpMethod
        - uri: String
        - parameters: Map<String, String>
        - protocol: String
        - headers: Map<String, String>
        - cookie: Cookie
        - body: String
    }

    class HttpResponse {
        - logger: Logger
        - protocol: String
        - statusCode: StatusCode
        - headers: Map<String, String>
        - setCookie: SetCookie
        - body: byte[]
    }
    
    class ServletManager {
        - Servlets: Map<String, Servlet>
    }
    
    class User{
        - userId: String
        - password: String
        - name: String
        - email: String
    }
    
    class Database{
        - users: Map<String, User>
    }

    WebServer "1" -- "0..*" RequestHandler : creates
    WebServer *-- "1" ServletManager : create
    ServletManager "1" -- "*" HttpRequest : uses
    ServletManager "1" -- "*" HttpResponse : uses
    RequestHandler --> ServletManager : use
    ServletManager --> HttpRequestFactory : use
    HttpRequestFactory *-- HttpRequest : create
    HttpRequestFactory --> CookieFactory : use
    ServletManager *-- DispatcherServlet
    DispatcherServlet --> HandlerMapping
    DispatcherServlet --> HandlerAdaptor
    DispatcherServlet "1" -- "0..*" Controller
    Controller "0..*" -- "1" Database
    Controller --> User
    Database "1" -- "0..*" User
```

## 추가적으로 배운 것
### mockito-junit-jupiter

- `@Mock` 어노테이션으로 간편하게 모킹을 지원하도록 하는 라이브러리.
- 테스트 클래스 위에 `@ExtendWith(MockitoExtension.class)` 를 추가한다.

### 생성자는 stubbing 이 불가능하다.

- Creator 패턴에서는 되도록이면 이미 사용하는 객체가 생성하는 것이 좋다고 하지만,
- 생성자의 책임이 지나치게 무거워지면 (ex: 예외를 던진다거나, 비즈니스 로직을 처리한다거나) 책임을 분리하는것이 좋다.

### ConcurrentHashMap 이 key, value로 null을 지원하지 않는 이유

#### HashMap은 왜 null 키/값을 허용할까?

- HashMap
  - 동시 접근에 대한 별도의 제어가 필요 없는 상황에서 사용하기 위한 자료구조
  - 따라서 키/값이 null이더라도 특별히 복잡한 동기화나 상태 관리 로직이 필요하지 않음
- 단일 스레드 환경에서의 null
  - 실제로 의미 있는 값일 수 있음 → "아직 결정되지 않음"
  - Map에 null 값을 저장해 활용할 수 있음
- 키 존재 여부와 값이 null인 경우를 구분할 필요가 없는 경우가 많음
  - 해당 변경사항은 자신이 직접 결정한 변경사항이므로, 논리적으로 오류가 없을 가능성이 높음.

---

#### ConcurrentHashMap은 왜 null 키/값을 허용하지 않을까?

- 동시성 환경의 특징
  - ConcurrentHashMap은 기본적으로 동시성 환경에서 동작을 보장
  - 이때, `map.get(key)`가 null을 리턴하면
    - 키가 제거된건지
    - 키의 값이 null인건지 구분해야 함
- 단일 의미를 가지지 않기 때문에, **다른 스레드**가 해당 값이 무슨 의미를 갖는지 알 수 없음.