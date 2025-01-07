# be-was-2025
코드스쿼드 백엔드 교육용 WAS 2025 개정판

# 학습 기록
[level 1](https://github.com/softeer5th/backend-page/wiki/%ED%95%9C%EC%A4%80%ED%98%B8_week1_day1)

# 학습해야 하는 내용
- LogBack
- HTTP 파싱

# 구현해야 하는 것
- HTTP 파싱
- ExecutorService 사용하도록 변경

# 도메인 모델
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