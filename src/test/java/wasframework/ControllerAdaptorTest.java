package wasframework;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import webserver.httpserver.HttpMethod;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.mockito.Mockito.*;

/**
 * Mockito 사용하기
 */


public class ControllerAdaptorTest {
    MockController controller;
    HttpRequest request;

    @BeforeEach
    void setUp() {
        controller = new MockController();
        request = mock(HttpRequest.class);
    }

    @Test
    @DisplayName("문자열 Path Variable 테스트")
    public void stringPathVariableTest() throws NoSuchMethodException, InvocationTargetException {
        // given
        Method method = MockController.class.getMethod(
                "stringPathVariableHandler", String.class, HttpResponse.class);
        ControllerMethod controllerMethod = new ControllerMethod(controller, method);
        HttpResponse response = new HttpResponse();
        when(request.getUri()).thenReturn("/users/helloWorld");

        // when
        ControllerAdaptor adaptor = new ControllerAdaptor();
        adaptor.invoke(controllerMethod, request, response);

        // then
        Assertions.assertThat(response.getBody()).isEqualTo("helloWorld".getBytes());
        verify(request).getUri();
    }

    @Test
    @DisplayName("int Path Variable 테스트")
    public void intPathVariableTest() throws NoSuchMethodException, InvocationTargetException {
        // given
        Method method = MockController.class.getMethod(
                "intPathVariableHandler", int.class, HttpResponse.class);
        ControllerMethod controllerMethod = new ControllerMethod(controller, method);
        HttpResponse response = new HttpResponse();
        when(request.getUri()).thenReturn("/posts/20");

        // when
        ControllerAdaptor adaptor = new ControllerAdaptor();
        adaptor.invoke(controllerMethod, request, response);

        // then
        Assertions.assertThat(response.getBody()).isEqualTo("20".getBytes());
        verify(request).getUri();
    }

    @Test
    @DisplayName("private 메소드 매핑 예외처리 테스트")
    public void hiddenMappingTest() throws NoSuchMethodException {
        // given
        Method method = MockController.class.getDeclaredMethod(
                "hiddenHandler", HttpResponse.class);
        ControllerMethod controllerMethod = new ControllerMethod(controller, method);
        HttpResponse response = new HttpResponse();
        when(request.getUri()).thenReturn("/posts/20");

        // when
        ControllerAdaptor adaptor = new ControllerAdaptor();

        Assertions.assertThatThrownBy(()->adaptor.invoke(controllerMethod, request, response))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("서버의 매핑이 잘못됨 - public 이 아닌 메소드에 @Mapping 사용 중");

    }

    // 테스트용 컨트롤러 클래스
    static class MockController {
        @Mapping(path = "/users/{userId}", method = HttpMethod.GET)
        public void stringPathVariableHandler(
                @PathVariable("userId") String userId,
                HttpResponse response) {
            response.setBody(userId.getBytes());
        }

        @Mapping(path = "/posts/{postId}", method = HttpMethod.GET)
        public void intPathVariableHandler(
                @PathVariable("postId") int postId,
                HttpResponse response
        ) {
            response.setBody(String.valueOf(postId).getBytes());
        }
        @Mapping(path = "/hidden", method = HttpMethod.GET)
        private void hiddenHandler(HttpResponse response){
            response.setBody("hidden".getBytes());
        }
    }
}
