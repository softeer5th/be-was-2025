## 기능 정리

- [X] 정적인 html 파일 응답
- [X] request 내용을 읽고 파싱 이후 로거(log.debug)를 이용하여 출력
  <br/>
  <br/>

- [X] 다양한 Content-Type 응답
- [X] 존재하지 않는 파일 요청하는 경우 404 반환
  <br/>
  <br/>

- [X] 회원가입/로그인 메뉴 클릭시 해당 페이지로 이동
- [X] 회원가입 정보를 User model에 저장
  <br/>
  <br/>

- [X] 회원가입 완료 시 메인페이지로 이동
  - [X] 302, location 반환하여 index/html으로 리다이랙션


- [X] 회원가입 시 userId 중복체크
  <br/>
  <br/>

- [X] 로그인 시 sid 쿠키 발급
- [X] 로그아웃 시 세션 정보 삭제, 쿠키 sid=null 으로 재발급
  <br/>
  <br/>


- [X] 로그인 시 메인페이지에 닉네임 버튼 생성
  - [X] 닉네임 버튼 클릭 시 myPage로 연결
- [X] 로그인 시 메인페이지에 로그아웃 버튼 생성


### 피드백 반영
- [X] request header 이름 case-insensitive
- [X] Logger static 으로 설정
- [ ] 이중 try catch 수정할것
- [ ] 예외처리 한곳에서 할 것


## 학습 정리
[DAY 1](https://github.com/softeer5th/backend-page/wiki/1%EC%9D%BC%EC%B0%A8_%EC%A0%84%EA%B2%BD%EC%84%9D)<br/>
[DAY 2](https://github.com/softeer5th/backend-page/wiki/2%EC%9D%BC%EC%B0%A8_%EC%A0%84%EA%B2%BD%EC%84%9D)<br/>
[DAY 3](https://github.com/softeer5th/backend-page/wiki/3%EC%9D%BC%EC%B0%A8_%EC%A0%84%EA%B2%BD%EC%84%9D)<br/>
[DAY 4](https://github.com/softeer5th/backend-page/wiki/4%EC%9D%BC%EC%B0%A8_%EC%A0%84%EA%B2%BD%EC%84%9D)<br/>
[DAY 5](https://github.com/softeer5th/backend-page/wiki/5%EC%9D%BC%EC%B0%A8_%EC%A0%84%EA%B2%BD%EC%84%9D)<br/>
[DAY 6](https://github.com/softeer5th/backend-page/wiki/6%EC%9D%BC%EC%B0%A8_%EC%A0%84%EA%B2%BD%EC%84%9D)<br/>
[DAY 7](https://github.com/softeer5th/backend-page/wiki/7%EC%9D%BC%EC%B0%A8_%EC%A0%84%EA%B2%BD%EC%84%9D)<br/>
[DAY 8](https://github.com/softeer5th/backend-page/wiki/8%EC%9D%BC%EC%B0%A8_%EC%A0%84%EA%B2%BD%EC%84%9D)<br/>
[DAY 9](https://github.com/softeer5th/backend-page/wiki/9%EC%9D%BC%EC%B0%A8_%EC%A0%84%EA%B2%BD%EC%84%9D)<br/>
[DAY 10](https://github.com/softeer5th/backend-page/wiki/10%EC%9D%BC%EC%B0%A8_%EC%A0%84%EA%B2%BD%EC%84%9D)<br/>



 