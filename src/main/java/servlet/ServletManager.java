package servlet;

import controller.HomeController;
import controller.SignUpController;
import exception.FileNotSupportedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webserver.httpserver.HttpRequest;
import webserver.httpserver.HttpResponse;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServletManager {
    private static final String NOT_FOUND = "NOT_FOUND";
    private static final String FILE_NOT_SUPPORTED = "FILE_NOT_SUPPORTED";
    private static final String BAD_REQUEST = "BAD_REQUEST";
    private static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    private static final String DISPATCHER = "dispatcher";
    private static final String DEFAULT = "default";
    private static final Logger log = LoggerFactory.getLogger(ServletManager.class);
    private final Map<String, Servlet> servlets = new HashMap<>();
    public ServletManager(List<Object> controllers) {
        servlets.put(DEFAULT, new StaticResourceServlet());
        servlets.put(DISPATCHER, new DispatcherServlet(controllers));
        servlets.put(FILE_NOT_SUPPORTED, new FileNotSupportedErrorPageServlet());
        servlets.put(NOT_FOUND, new FileNotFoundPageServlet());
        servlets.put(BAD_REQUEST, new BadRequestServlet());
        servlets.put(INTERNAL_SERVER_ERROR, new InternalServerErrorServlet());
    }

    public void addServlet(String url, Servlet servlet) {
        servlets.put(url, servlet);
    }

    /**
     * 서블릿 매니저가 HTTP 요청을 서빙하는 메소드.
     * 먼저 Dispatcher 서블릿에게 동적 리소스 핸들링을 맡겨보고, 만약 제대로 처리가 안됐다면 (false 반환) 정적 리소스를 탐색한다.
     * HTTP 메시지 파싱 중 예외가 발생하면 Bad Request 를 응답으로 기록하고 서빙을 종료한다.
     * 정적 리소스 서빙 중 예외가 발생하면 발생한 예외에 따라 적절한 페이지를 응답한다.
     * FileNotSupportedException: 해당 확장자가 지원되지 않을 경우, 406 응답 반환
     * FileNotFoundException: 파일이 존재하지 않을 경우, 404 응답 반환
     * IllegalArgumentException: URI 가 비어있을 경우, 400 응답 반환
     * IOException (Except FileNotFound): 스트림이 도중에 닫히거나, 권한이 없을 경우 등. 400 응답 반환
     * Exception: 그 외 서버에서 예상하지 못한 RuntimeException 처리
     * 현재 HTTP 메시지를 생성하고 전달하는 책임 + 예외를 처리하는 책임 = 2가지 책임을 지니고 있음.
     *      - 분리하면 ExceptionHandler -> 스프링
     * @param bis BufferedInputStream
     * @param dos DataOutputStream
     * @throws IOException 반드시 서빙해야 하는 파일(예외 페이지) 이 존재하지 않을 경우
     */
    public void serve(BufferedInputStream bis, DataOutputStream dos) throws IOException {
        HttpRequest request = null;
        HttpResponse response = new HttpResponse();
        request = getHttpRequest(bis, dos, request, response);
        if (request == null) return;
        handleRequest(request, response);
        response.send(dos);
    }

    private HttpRequest getHttpRequest(BufferedInputStream bis, DataOutputStream dos, HttpRequest request, HttpResponse response) throws IOException {
        try {
            request = new HttpRequest(bis);
            response.setProtocol(request.getProtocol());
        } catch (IOException e) {
            response.setProtocol("HTTP/1.1");
            servlets.get(BAD_REQUEST).handle(request, response);
            response.send(dos);
            return null;
        }
        return request;
    }

    private void handleRequest(HttpRequest request, HttpResponse response) throws IOException {
        try {
            if (!servlets.get(DISPATCHER).handle(request, response)) {
                servlets.get(DEFAULT).handle(request, response);
            }
        } catch (FileNotSupportedException e) {
            servlets.get(FILE_NOT_SUPPORTED).handle(request, response);
        } catch (FileNotFoundException e){
            servlets.get(NOT_FOUND).handle(request, response);
        } catch (IOException | IllegalArgumentException e) {
            servlets.get(BAD_REQUEST).handle(request, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            servlets.get(INTERNAL_SERVER_ERROR).handle(request, response);
        }
    }
}
