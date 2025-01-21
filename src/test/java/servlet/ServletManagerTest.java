package servlet;

import exception.FileNotSupportedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpRequestFactory;
import webserver.httpserver.HttpResponse;

import java.io.*;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static servlet.ServletManager.*;

@ExtendWith(MockitoExtension.class)
class ServletManagerTest {

    @Mock
    private DispatcherServlet dispatcherServlet;
    @Mock
    private StaticResourceServlet staticResourceServlet;
    @Mock
    private FileNotSupportedErrorPageServlet fileNotSupportedServlet;
    @Mock
    private FileNotFoundPageServlet fileNotFoundServlet;
    @Mock
    private BadRequestServlet badRequestServlet;
    @Mock
    private InternalServerErrorServlet internalServerErrorServlet;
    @Mock
    private HttpRequestFactory requestFactory;

    @Mock
    private BufferedInputStream bis;

    private ServletManager manager;

    @BeforeEach
    void setUp() throws IllegalAccessException, NoSuchFieldException {
        manager = new ServletManager(List.of(), requestFactory);


        Map<String, Servlet> mockServletMap = new HashMap<>();
        mockServletMap.put(DEFAULT, staticResourceServlet);
        mockServletMap.put(DISPATCHER, dispatcherServlet);
        mockServletMap.put(FILE_NOT_SUPPORTED, fileNotSupportedServlet);
        mockServletMap.put(NOT_FOUND, fileNotFoundServlet);
        mockServletMap.put(BAD_REQUEST, badRequestServlet);
        mockServletMap.put(INTERNAL_SERVER_ERROR, internalServerErrorServlet);

        Field servletsField = ServletManager.class.getDeclaredField("servlets");
        servletsField.setAccessible(true);
        servletsField.set(manager, mockServletMap);
    }

    @Test
    @DisplayName("동적 리소스 서빙 성공")
    void testServe_normalHttp() throws IOException {
        // given
        HttpRequest request = mock(HttpRequest.class);
        when(requestFactory.getHttpRequest(bis)).thenReturn(request);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(dispatcherServlet.handle(any(HttpRequest.class), any(HttpResponse.class)))
                .thenReturn(true);
        // when
        HttpResponse response = manager.serve(bis);

        // then
        verify(dispatcherServlet, times(1)).handle(any(HttpRequest.class), any(HttpResponse.class));
        verify(staticResourceServlet, never()).handle(any(HttpRequest.class), any(HttpResponse.class));
        verify(fileNotSupportedServlet, never()).handle(any(), any());
        verify(fileNotFoundServlet, never()).handle(any(), any());
        verify(badRequestServlet, never()).handle(any(), any());
        verify(internalServerErrorServlet, never()).handle(any(), any());
    }

    @Test
    @DisplayName("request 생성 중 IOE 예외 발생")
    void testServe_requestCreationIOException() throws IOException {
        // given
        doThrow(new IOException())
                .when(requestFactory).getHttpRequest(bis);

        // when
        manager.serve(bis);

        // then
        verify(badRequestServlet).handle(isNull(), any(HttpResponse.class));
        verify(dispatcherServlet, never()).handle(any(), any());
        verify(staticResourceServlet, never()).handle(any(), any());
    }

    @Test
    @DisplayName("request 생성 중 IllegalArgumentException 예외 발생")
    void testServe_requestCreationIllegalArgumentException() throws IOException {
        // given
        doThrow(new IllegalArgumentException())
                .when(requestFactory).getHttpRequest(bis);

        // when
        manager.serve(bis);

        // then
        verify(badRequestServlet).handle(isNull(), any(HttpResponse.class));
        verify(dispatcherServlet, never()).handle(any(), any());
        verify(staticResourceServlet, never()).handle(any(), any());
    }


    @Test
    @DisplayName("정적 리소스 서빙 성공")
    void testServe_dispatcherReturnsFalseThenStaticResource() throws IOException {
        // given
        HttpRequest request = mock(HttpRequest.class);
        when(requestFactory.getHttpRequest(bis)).thenReturn(request);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(dispatcherServlet.handle(any(HttpRequest.class), any(HttpResponse.class)))
                .thenReturn(false);

        // when
        manager.serve(bis);

        // then
        verify(dispatcherServlet, times(1)).handle(any(), any());
        verify(staticResourceServlet, times(1)).handle(any(), any());
        verify(fileNotSupportedServlet, never()).handle(any(), any());
        verify(fileNotFoundServlet, never()).handle(any(), any());
        verify(badRequestServlet, never()).handle(any(), any());
        verify(internalServerErrorServlet, never()).handle(any(), any());
    }

    @Test
    @DisplayName("File Not Supported 예외 발생")
    void testHandleRequest_FileNotSupported() throws IOException {
        // given
        basicSetting();
        doThrow(new FileNotSupportedException())
                .when(staticResourceServlet).handle(any(), any());

        // when
        manager.serve(bis);

        // then
        verify(fileNotSupportedServlet, times(1)).handle(any(HttpRequest.class), any(HttpResponse.class));
        verify(fileNotFoundServlet, never()).handle(any(), any());
        verify(badRequestServlet, never()).handle(any(), any());
        verify(internalServerErrorServlet, never()).handle(any(), any());
    }

    @Test
    @DisplayName("File Not Found 예외 발생")
    void testHandleRequest_FileNotFound() throws IOException {
        // given
        basicSetting();
        doThrow(new FileNotFoundException())
                .when(staticResourceServlet).handle(any(), any());

        // when
        manager.serve(bis);

        // then
        verify(fileNotFoundServlet, times(1)).handle(any(HttpRequest.class), any(HttpResponse.class));
        verify(fileNotSupportedServlet, never()).handle(any(), any());
        verify(badRequestServlet, never()).handle(any(), any());
        verify(internalServerErrorServlet, never()).handle(any(), any());
    }

    private void basicSetting() throws IOException {
        HttpRequest request = mock(HttpRequest.class);
        when(requestFactory.getHttpRequest(bis)).thenReturn(request);
        when(request.getProtocol()).thenReturn("HTTP/1.1");
        when(dispatcherServlet.handle(any(), any())).thenReturn(false);
    }

    @Test
    @DisplayName("FileNotFound제외 IOE 예외 발생")
    void testHandleRequest_IOException() throws IOException {
        // given
        basicSetting();
        doThrow(new IOException())
                .when(staticResourceServlet).handle(any(), any());

        // when
        manager.serve(bis);

        // then
        verify(badRequestServlet, times(1)).handle(any(HttpRequest.class), any(HttpResponse.class));
        verify(fileNotSupportedServlet, never()).handle(any(), any());
        verify(fileNotFoundServlet, never()).handle(any(), any());
        verify(internalServerErrorServlet, never()).handle(any(), any());
    }

    @Test
    @DisplayName("정적 리소스 서빙 중 ContentType 추측 실패 - 예외 발생")
    void testHandleRequest_IllegalArgumentException() throws IOException {
        // given
        basicSetting();
        doThrow(new IllegalArgumentException())
                .when(staticResourceServlet).handle(any(), any());

        // when
        manager.serve(bis);

        // then
        verify(badRequestServlet, times(1)).handle(any(HttpRequest.class), any(HttpResponse.class));
        verify(fileNotSupportedServlet, never()).handle(any(), any());
        verify(fileNotFoundServlet, never()).handle(any(), any());
        verify(internalServerErrorServlet, never()).handle(any(), any());
    }

    @Test
    @DisplayName("서버 내부 예외 발생")
    void testHandleRequest_OtherException() throws IOException {
        // given
        basicSetting();
        doThrow(new RuntimeException())
                .when(staticResourceServlet).handle(any(), any());

        // when
        manager.serve(bis);

        // then
        verify(internalServerErrorServlet, times(1)).handle(any(HttpRequest.class), any(HttpResponse.class));
        verify(fileNotSupportedServlet, never()).handle(any(), any());
        verify(fileNotFoundServlet, never()).handle(any(), any());
        verify(badRequestServlet, never()).handle(any(), any());
    }
}
