# **학습한 내용**

# 웹서버 1단계

## **1. Thread**

---

| **이름**              | **관리자** | **메모리** | **생성 비용** | **Context Switching** |
|---------------------|---------|---------|-----------|-----------------------|
| **Kernel Thread**   | OS      | 약 1MB   | 높음        | 느림                    |
| **Platform Thread** | JVM     | 약 512KB | 중간        | 중간                    |
| **Virtual Thread**  | JVM     | 약 1KB   | 낮음        | 빠름                    |

---

- **Java 20 이전: Platform Thread**

Kernel Thread 와 1:1 매핑

Context Switching 비용으로 인한 성능 저하 → **C10K Problem**

대안: Reactive Programming → 러닝커브 높음.

---

- **Java 21 이후: Virtual Thread**

Platform Thread 와 1:N 매핑

Virtual Thread를 실행시키는 Platform Thread를 Carrier Thread라고 부름

경량화 되었고 기존 Thread와도 호환 → 추가 기술 없이 적용 가능

**synchronized** 키워드 사용 시 **Carrier Thread를 점유하는 Pinning** 문제가 발생 → ReentrantLock 사용

Thread Pool 없이 사용 권장

## 2. java.utill.concurrent

동시성 제어를 위한 라이브러리 패키지

- **ExecutorService**

일종의 Thread Pool. Executors 클래스를 통해 생성. Runnable 객체를 실행 가능

내부적으로 Thread를 재사용

- **Future**

비동기 작업의 결과값. get() 메서드로 결과값을 기다렸다(Blocking) 얻을 수 있음

- **CountDownLatch**

여러 Thread 가 작업을 동시에 시작하도록 하는 동기화 클래스

- **CyclicBarrier**

CountDownLatch 와 유사하지만 재사용 가능

- **Semaphore**

동시에 실행 가능한 Thread 의 수를 제한하는 동기화 클래스

- **Lock**

동시에 실행 가능한 Thread 의 수를 1개로 제한하는 동기화 클래스

## 3. HTTP 구조

- HTTP Request

```
POST /users HTTP/1.1
Host: example.com
Connection: keep-alive
Content-Type: application/json
Cotnent-Length: 42
Accept: */*

{
	"id": "id1",
	"password": "password1"
}
```

- HTTP Response

```
HTTP/1.1 201 Created
Content-Type: application/json
Content-Length: 154
Location: http://example.com/users/123

{
  "message": "New user created",
  "user": {
    "id": 123,
    "firstName": "Example",
    "lastName": "Person",
    "email": "bsmth@example.com"
  }
}
```