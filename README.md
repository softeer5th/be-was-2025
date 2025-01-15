# be-was-2025
코드스쿼드 백엔드 교육용 WAS 2025 개정판

# 과제 학습내용
- [1차 과제 학습 내용](https://github.com/softeer5th/backend-page/wiki/%5BWeek1%5D-%EC%86%A1%EB%AF%BC%EA%B7%9C)
- [2차 과제 학습 내용]()

# 1차 과제

## 기존 코드의 구조 변경
* [x] 자바 스레드 모델에 대해 학습한다. 버전 별로 어떤 변경점이 있었는지와 향후 지향점에 대해서도 학습해 본다.
* [x] 자바 Concurrent 패키지에 대해 학습한다.
* [x] 기존의 프로젝트는 Java Thread 기반으로 작성되어 있다. 이를 Concurrent 패키지를 사용하도록 변경한다.

## OOP와 클린 코딩
* [x] 주어진 소스 코드를 기반으로 기능요구사항을 만족하는 코드를 작성한다.
* [x] 유지보수에 좋은 구조에 대해 고민하고 코드를 개선해 본다.
* [ ] 웹 리소스 파일은 제공된 파일을 수정해서 사용한다. (직접 작성해도 무방함)

# 2차 과제
* [x] 2차 과제 구현
* [x] HTTP Response 에 대해 학습한다. 
* [x] MIME 타입에 대해 이해하고 이를 적용할 수 있다.

# 3차 과제
* [x] HTTP GET 프로토콜을 이해한다. 
* [x] HTTP GET에서 parameter를 전달하고 처리하는 방법을 학습한다. 
* [x] HTTP 클라이언트에서 전달받은 값을 서버에서 처리하는 방법을 학습한다.
  * 기본적인 기능 구현 
  * 회원가입 완료 -> Redirection 구현 
  * 서블릿 구현 고민 
  * 테스트코드 작성

# 4차 과제
## 기능요구사항
* [x] 로그인을 GET에서 POST로 수정 후 정상 동작하도록 구현한다.
* [x] GET으로 회원가입을 시도할 경우 실패해야 한다.
* [x] 가입을 완료하면 /index.html 페이지로 이동한다.

## 프로그래밍 요구사항
* [x] POST로 수정
* [x] 회원가입 html 파일의 form 태그 내 method를 get에서 post로 수정한다.
* [x] 나머지 회원가입 기능이 정상적으로 동작하도록 구현한다.
* [x] 가입 후 페이지 이동을 위해 HTTP redirection 기능을 구현한다.

## 개인 할일
- [x] [HTTP Method 스펙 공부](https://gamxong.tistory.com/157)
- [ ] HTTP HEAD 메서드 구현
- [x] HTTP POST 스펙 공부
- [ ] Redirection 스펙 공부
- [x] HTTP body 스펙 공부
