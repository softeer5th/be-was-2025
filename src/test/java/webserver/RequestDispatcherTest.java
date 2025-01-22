package webserver;

import fixtureUtil.ClientExceptionRouter;
import fixtureUtil.ServerExceptionRouter;
import fixtureUtil.TestRouter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.*;
import java.net.Socket;

import static org.mockito.Mockito.when;

class RequestDispatcherTest {

    @Test
    @DisplayName("핸들러에서 정상적인 응답을 반환하면 요청을 클라이언트에 전송한다.")
    void run() throws IOException {
        // given
        Socket connection = Mockito.mock(Socket.class);
        RequestDispatcher requestDispatcher = new RequestDispatcher(connection, new TestRouter());

        String httpRequest = """
                POST /test HTTP/1.1\r
                Accept: application/json\r
                Accept-Encoding: gzip, deflate\r
                Connection: keep-alive\r
                Content-Length: 4\r
                Content-Type: application/json\r
                Host: google.com\r
                User-Agent: HTTPie/0.9.3\r
                \r
                gigi\r
                """;

        byte[] httpRequestBytes = httpRequest.getBytes();

        InputStream mockInputStream = new ByteArrayInputStream(httpRequestBytes);
        OutputStream mockOutputStream = new ByteArrayOutputStream();

        when(connection.getInputStream())
                .thenReturn(mockInputStream);
        when(connection.getOutputStream())
                .thenReturn(mockOutputStream);

        final String expected = """
                HTTP/1.1 200 Ok \r
                Content-Length: 10\r
                Content-Type: text/html; charset=utf-8\r
                \r
                test pass!\r
                """;

        // when
        requestDispatcher.run();

        // then
        Assertions.assertThat(mockOutputStream.toString())
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("핸들러에서 client exception을 던지면 4xx 응답을 클라이언트에 전송한다.")
    void run_clientException() throws IOException {
        // given
        Socket connection = Mockito.mock(Socket.class);
        RequestDispatcher requestDispatcher = new RequestDispatcher(connection, new ClientExceptionRouter());


        String httpRequest = """
                POST /test HTTP/1.1\r
                Accept: application/json\r
                Accept-Encoding: gzip, deflate\r
                Connection: keep-alive\r
                Content-Length: 4\r
                Content-Type: application/json\r
                Host: google.com\r
                User-Agent: HTTPie/0.9.3\r
                \r
                gigi\r
                """;

        byte[] httpRequestBytes = httpRequest.getBytes();

        InputStream mockInputStream = new ByteArrayInputStream(httpRequestBytes);
        OutputStream mockOutputStream = new ByteArrayOutputStream();

        when(connection.getInputStream())
                .thenReturn(mockInputStream);
        when(connection.getOutputStream())
                .thenReturn(mockOutputStream);

        final String expected = """
                 HTTP/1.1 400 Bad Request
                       Content-Length: 1506
                       Content-Type: text/html; charset=utf-8
                
                       <!DOCTYPE html>
                       <html lang="ko">
                       <head>
                         <meta charset="UTF-8"/>
                         <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                         <link href="../reset.css" rel="stylesheet"/>
                         <link href="../global.css" rel="stylesheet"/>
                         <title>에러 발생</title>
                         <style>
                           body {
                             font-family: Arial, sans-serif;
                             background-color: #f4f6f9;
                             color: #333;
                             display: flex;
                             justify-content: center;
                             align-items: center;
                             height: 100vh;
                             margin: 0;
                           }
                           .error-container {
                             text-align: center;
                             padding: 30px;
                             border-radius: 8px;
                             background-color: #fff;
                             box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                             max-width: 500px;
                             width: 100%;
                           }
                           .error-container h1 {
                             font-size: 96px;
                             color: #e74c3c;
                             margin: 0;
                           }
                           .error-container p {
                             font-size: 18px;
                             margin: 20px 0;
                             color: #555;
                           }
                           .btn {
                             display: inline-block;
                             padding: 12px 20px;
                             background-color: #3498db;
                             color: white;
                             text-decoration: none;
                             border-radius: 5px;
                             font-weight: bold;
                             transition: background-color 0.3s;
                           }
                           .btn:hover {
                             background-color: #2980b9;
                           }
                         </style>
                       </head>
                       <body>
                
                       <div class="error-container">
                         <h1>400</h1>
                         <p>잘못된 HTTP 요청입니다.</p>
                         <p>잠시 후 다시 시도하거나, 아래 버튼을 클릭하여 홈으로 돌아가세요.</p>
                
                         <a href="/" class="btn">홈으로 돌아가기</a>
                       </div>
                
                       </body>
                       </html>
                 """;

        // when
        requestDispatcher.run();

        // then 글자 구성만 같으면 Pass(공백 무시)
        Assertions.assertThat(mockOutputStream.toString().trim().replaceAll("\\s+", "").trim())
                .isEqualTo(expected.replaceAll("\\s+", "").trim());
    }

    @Test
    @DisplayName("핸들러에서 server exception을 던지면 4xx 응답을 클라이언트에 전송한다.")
    void run_serverException() throws IOException {
        // given
        Socket connection = Mockito.mock(Socket.class);
        RequestDispatcher requestDispatcher = new RequestDispatcher(connection, new ServerExceptionRouter());

        String httpRequest = """
                POST /test HTTP/1.1\r
                Accept: application/json\r
                Accept-Encoding: gzip, deflate\r
                Connection: keep-alive\r
                Content-Length: 4\r
                Content-Type: application/json\r
                Host: google.com\r
                User-Agent: HTTPie/0.9.3\r
                \r
                gigi\r
                """;

        byte[] httpRequestBytes = httpRequest.getBytes();

        InputStream mockInputStream = new ByteArrayInputStream(httpRequestBytes);
        OutputStream mockOutputStream = new ByteArrayOutputStream();

        when(connection.getInputStream())
                .thenReturn(mockInputStream);
        when(connection.getOutputStream())
                .thenReturn(mockOutputStream);

        final String expected = """
                HTTP/1.1 500 Internal Server Error
                Content-Length: 1510
                Content-Type: text/html; charset=utf-8
                
                <!DOCTYPE html>
                <html lang="ko">
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
                  <link href="../reset.css" rel="stylesheet"/>
                  <link href="../global.css" rel="stylesheet"/>
                  <title>에러 발생</title>
                  <style>
                    body {
                      font-family: Arial, sans-serif;
                      background-color: #f4f6f9;
                      color: #333;
                      display: flex;
                      justify-content: center;
                      align-items: center;
                      height: 100vh;
                      margin: 0;
                    }
                    .error-container {
                      text-align: center;
                      padding: 30px;
                      border-radius: 8px;
                      background-color: #fff;
                      box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                      max-width: 500px;
                      width: 100%;
                    }
                    .error-container h1 {
                      font-size: 96px;
                      color: #e74c3c;
                      margin: 0;
                    }
                    .error-container p {
                      font-size: 18px;
                      margin: 20px 0;
                      color: #555;
                    }
                    .btn {
                      display: inline-block;
                      padding: 12px 20px;
                      background-color: #3498db;
                      color: white;
                      text-decoration: none;
                      border-radius: 5px;
                      font-weight: bold;
                      transition: background-color 0.3s;
                    }
                    .btn:hover {
                      background-color: #2980b9;
                    }
                  </style>
                </head>
                <body>
                
                <div class="error-container">
                  <h1>500</h1>
                  <p>인코딩에 실패하였습니다.</p>
                  <p>잠시 후 다시 시도하거나, 아래 버튼을 클릭하여 홈으로 돌아가세요.</p>
                
                  <a href="/" class="btn">홈으로 돌아가기</a>
                </div>
                
                </body>
                </html>
                
                """;

        // when
        requestDispatcher.run();

        // then 글자 구성만 같으면 Pass(공백 무시)
        Assertions.assertThat(mockOutputStream.toString().replaceAll("\\s+", "").trim())
                .isEqualTo(expected.replaceAll("\\s+", "").trim());
    }
}