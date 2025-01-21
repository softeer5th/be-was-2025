package servlet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import wasframework.ControllerAdaptor;
import wasframework.ControllerMapping;
import wasframework.ControllerMethod;
import webserver.httpserver.HttpMethod;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DispatcherServletTest {

    @Mock
    private ControllerMapping controllerMapping;

    @Mock
    private ControllerAdaptor controllerAdaptor;

    @Mock
    private HttpRequest request;

    @Mock
    private HttpResponse response;

    private DispatcherServlet dispatcherServlet;

    @BeforeEach
    void setUp() {
        dispatcherServlet = new DispatcherServlet(List.of());

        // 리플렉션 메소드를 이용한 필드 주입
        injectMockField("controllerMapping", controllerMapping);
        injectMockField("controllerAdaptor", controllerAdaptor);
    }

    @Test
    @DisplayName("컨트롤러 메소드가 없을 때")
    void noMethodFound() throws InvocationTargetException {
        // given
        when(request.getUri()).thenReturn("/test");
        when(request.getMethod()).thenReturn(HttpMethod.GET);
        when(controllerMapping.getControllerMethod("/test", HttpMethod.GET)).thenReturn(null);

        // when
        boolean result = dispatcherServlet.handle(request, response);

        // then
        assertThat(result).isFalse();
        verify(controllerAdaptor, never()).invoke(any(), any(), any());
    }

    @Test
    @DisplayName("컨트롤러 메소드가 있을 떄")
    void methodFound_noException() throws InvocationTargetException {
        // given
        when(request.getUri()).thenReturn("/test");
        when(request.getMethod()).thenReturn(HttpMethod.GET);

        ControllerMethod mockControllerMethod = new ControllerMethod(null, null);
        when(controllerMapping.getControllerMethod("/test", HttpMethod.GET))
                .thenReturn(mockControllerMethod);

        // when
        boolean result = dispatcherServlet.handle(request, response);

        // then
        assertThat(result).isTrue();
        verify(controllerAdaptor).invoke(eq(mockControllerMethod), eq(request), eq(response));
    }

    @Test
    @DisplayName("컨트롤러 메소드 호출 중 예외 발생 시")
    void invocationThrowsException() throws InvocationTargetException {
        // given
        when(request.getUri()).thenReturn("/test");
        when(request.getMethod()).thenReturn(HttpMethod.GET);

        ControllerMethod mockControllerMethod = new ControllerMethod(null, null);
        when(controllerMapping.getControllerMethod("/test", HttpMethod.GET))
                .thenReturn(mockControllerMethod);

        doThrow(new InvocationTargetException(new RuntimeException("testException")))
                .when(controllerAdaptor).invoke(any(), any(), any());

        // when
        boolean result = dispatcherServlet.handle(request, response);

        // then
        assertThat(result).isFalse();
    }


    //DispatcherServlet 내 private 필드에 Mock 객체를 주입하기 위한 편의 메소드
    private void injectMockField(String fieldName, Object mockInstance) {
        try {
            Field field = DispatcherServlet.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(dispatcherServlet, mockInstance);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
